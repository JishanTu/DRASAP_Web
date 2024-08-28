package tyk.drasap.springfw.controller;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import tyk.drasap.springfw.bean.Action;
import tyk.drasap.springfw.bean.ActionMappings;
import tyk.drasap.springfw.bean.Forward;
import tyk.drasap.springfw.bean.GlobalForwards;
import tyk.drasap.springfw.form.Validatable;

@Aspect
@Component
public class AspectController {
	private static final Logger category = Logger.getLogger(AspectController.class.getName());

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private GlobalForwards globalForwards;

	@Autowired
	private ActionMappings actionMappings;

	public AspectController() {
		System.out.println("AspectController start ......");
		category.debug("start");
		category.debug("end");
	}

	@Pointcut("@within(org.springframework.stereotype.Controller) && !within(tyk.drasap.springfw.controller.SwitchController)")
	public void withinController() {
	}

	@Pointcut("execution(* tyk.drasap.springfw.controller.SwitchController.*(..))")
	public void executeSwitchController() {
	}

	/**
	 *
	 * @param joinPoint
	 * @return
	 * @throws Throwable
	 */
	@Around("withinController()")
	public Object withinAction(ProceedingJoinPoint joinPoint) throws Throwable {
		Object actResult = null;
		Validatable form = null;
		Model errors = null;

		// �T�[�u���b�g���N�G�X�g�����擾
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		HttpServletResponse response = attributes.getResponse();

		// �����`�F�b�N
		Object[] args = joinPoint.getArgs();
		for (Object arg : args) {
			if (arg instanceof Validatable) {
				form = (Validatable) arg;
			} else if (arg instanceof Model) {
				errors = (Model) arg;
			}
		}

		// ���L�����𖞂������ꍇ
		// �E�t�H�[����null�łȂ�
		// �E���f����null�łȂ�
		if (Objects.nonNull(form) && Objects.nonNull(errors)) {
			// �`�F�b�N���{�O�Ƀ��f���̃T�C�Y
			int inputSize = errors.asMap().size();

			// validate���\�b�h�����{
			form.validate(request, errors, messageSource);

			// �T�C�Y�ς�������߃`�F�b�N�G���[����ƌ��Ȃ�
			if (errors.asMap().size() != inputSize) {
				actResult = "failed";
			}
		}

		// �`�F�b�N�G���[�Ȃ�
		if (Objects.isNull(actResult)) {
			actResult = joinPoint.proceed();
		}

		// access log
		accessLog("+++  withinAction  ", request, actResult);

		// ���ʂ�������̏ꍇ
		if (actResult instanceof String) {
			String result = (String) actResult;

			// ServletPath�擾
			String actPath = request.getServletPath();

			// �J�ڐ��T��
			result = findForward(result, actPath);
			if (StringUtils.isNotEmpty(result)) {
				// �J�ڐ��.jsp���܂܂ꂽ�ꍇ
				if (result.contains(".jsp")) {
					setRequestAttribute(request, result);
					String[] parts = result.split(".jsp");
					actResult = parts[0];
					// �J�ڐ��.do���܂܂ꂽ�ꍇ
				} else if (result.contains(".do")) {
					// Action���s
					actResult = doAction(result, form, request, response, errors);
				}
			} else {
				actResult = result;
			}
		}
		return actResult;
	}

	/**
	 *
	 * @param joinPoint
	 * @param path
	 * @param errors
	 * @return
	 * @throws Throwable
	 */
	@Around(value = "executeSwitchController() && args(path, errors)", argNames = "joinPoint,path,errors")
	public Object accessPath(ProceedingJoinPoint joinPoint, String path, Model errors) throws Throwable {
		// �T�[�u���b�g���N�G�X�g�����擾
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		HttpServletResponse response = attributes.getResponse();

		// switch.do�̏ꍇ
		if ("switch.do".equals(path)) {
			Map<String, String> queryParams = getQueryParams(request);
			String page = queryParams.get("page");

			// access log
			accessLog("###  accessPath    ", request, page);
			return page.replace(".jsp", "");
		}

		// Action���s
		String actPath = request.getServletPath();
		Object actResult = doAction(actPath, null, request, response, errors);
		return actResult;
	}

	/**
	 *
	 * @param forward
	 * @param form
	 * @param request
	 * @param response
	 * @param errors
	 * @return
	 */
	private Object doAction(String forward, Object form, HttpServletRequest request, HttpServletResponse response, Model errors) {
		Object actResult = null;
		if (Objects.isNull(actionMappings)) {
			return actResult;
		}
		setRequestAttribute(request, forward);

		// action-mappings.json�ɒ�`������ꍇ
		String[] parts = forward.split("\\?");
		int beginIndex = parts[0].lastIndexOf("/") == -1 ? 0 : parts[0].lastIndexOf("/");
		String path = parts[0].substring(beginIndex).replace(".do", "");
		for (Action action : actionMappings.getActions()) {
			if (path.equals(action.getPath())) {
				// doPathMethod���{
				actResult = doPathMethod(action, form, request, response, errors);
				break;
			}
		}

		// access log
		accessLog("***  doAction      ", request, actResult);

		// ���ʂ�������̏ꍇ
		if (actResult instanceof String) {
			String result = (String) actResult;

			// �J�ڐ��T��
			result = findForward(result, path);
			if (StringUtils.isNotEmpty(result)) {
				// �J�ڐ��.jsp���܂܂ꂽ�ꍇ
				if (result.contains(".jsp")) {
					setRequestAttribute(request, result);
					String[] resultParts = result.split(".jsp");
					actResult = resultParts[0];
					// �J�ڐ��.do���܂܂ꂽ�ꍇ
				} else if (result.contains(".do")) {
					actResult = doAction(result, form, request, response, errors);
				}
			} else {
				actResult = result;
			}
		}
		return actResult;
	}

	/**
	 *
	 * @param action
	 * @param form
	 * @param request
	 * @param response
	 * @param errors
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private Object doPathMethod(Action action, Object form, HttpServletRequest request, HttpServletResponse response, Model errors) {
		try {
			// QueryString����p�����[�^���擾���A���N�G�X�g��Attribute�ɐݒ�
			Map<String, String> queryParams = getQueryParams(request);
			for (Map.Entry<String, String> entry : queryParams.entrySet()) {
				request.setAttribute(entry.getKey(), entry.getValue());
			}

			// form��Class�ƃC���X�^���X���擾
			Class<?> formClass = Class.forName(action.getName());
			if (Objects.isNull(form) || !formClass.equals(form.getClass())) {
				form = formClass.newInstance();
			}

			Class<?> clazz = Class.forName(action.getType());
			Object instance = clazz.getDeclaredConstructor().newInstance();
			Method setMessage = clazz.getMethod("setMessageSource", MessageSource.class);
			Method execute = clazz.getMethod("execute", formClass, HttpServletRequest.class, HttpServletResponse.class, Model.class);

			// TODO reset()�ďo���O�����ǉ�
			//if ("RequestHistoryForm".equals(action.getAttribute()) || !(form instanceof tyk.drasap.genzu_irai.Request_listForm)) {
			if (!(form instanceof tyk.drasap.genzu_irai.Request_listForm)) {
				for (Method method : formClass.getMethods()) {
					if ("reset".equalsIgnoreCase(method.getName())) {
						method.invoke(form, request);
						break;
					}
				}
			}

			setMessage.invoke(instance, messageSource);
			Object result = execute.invoke(instance, form, request, response, errors);
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 *
	 * @param target
	 * @param actPath
	 * @return
	 */
	private String findForward(String target, String actPath) {
		String result = null;

		// �T���Ώۂ����ݒ�̏ꍇ
		if (StringUtils.isEmpty(target)) {
			return target;
		}

		// global-forwards����T��
		if (Objects.nonNull(globalForwards)) {
			for (Forward forward : globalForwards.getForwards()) {
				if (forward.getName().equals(target)) {
					result = forward.getPath();
				}
			}
		}

		if (StringUtils.isEmpty(result) && Objects.nonNull(actionMappings)) {
			for (Action action : actionMappings.getActions()) {
				// path���s��v�̏ꍇ�A����
				if (!actPath.equals(action.getPath())) {
					continue;
				}

				for (Forward fw : action.getForwards()) {
					// name����v�̏ꍇ
					if (target.equals(fw.getName())) {
						// forward��ݒ�
						result = fw.getPath();
						break;
					}
				}

				// ���������ꍇ
				if (StringUtils.isNotEmpty(result)) {
					break;
				}
			}
		}

		result = StringUtils.isEmpty(result) ? target : result;
		return result;
	}

	/**
	 *
	 * @param request
	 * @param forward
	 */
	private void setRequestAttribute(HttpServletRequest request, String forward) {
		Pattern pattern = Pattern.compile("[?&]([^=]+)=([^&]+)");
		Matcher matcher = pattern.matcher(forward);

		while (matcher.find()) {
			String key = matcher.group(1);
			String value = matcher.group(2);
			request.setAttribute(key, value);
		}
	}

	/**
	 *
	 * @param request
	 * @return
	 */
	private Map<String, String> getQueryParams(HttpServletRequest request) {
		String queryString = request.getQueryString();
		Map<String, String> queryParams = new HashMap<>();

		if (queryString == null || queryString.isEmpty()) {
			return queryParams;
		}

		String[] pairs = queryString.split("&");
		for (String pair : pairs) {
			int idx = pair.indexOf("=");
			try {
				String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
				String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
				queryParams.put(key, value);
			} catch (UnsupportedEncodingException e) {
				// Handle exception
				e.printStackTrace();
			}
		}

		return queryParams;
	}

	/**
	 *
	 * @param method
	 * @param request
	 * @param actResult
	 * @return
	 */
	private String accessLog(String method, HttpServletRequest request, Object actResult) {
		// �t��URL���擾
		StringBuffer requestURL = request.getRequestURL();
		String fullUrl = requestURL.toString();

		// �N�G����������擾
		String queryString = request.getQueryString();

		// �t��URL�ɃN�G���������ǉ�
		if (StringUtils.isNotEmpty(queryString)) {
			fullUrl += "?" + queryString;
		}

		// �߂�l����
		String result = "";
		if (Objects.isNull(actResult)) {
			result = "NULL";
		} else if (actResult instanceof String) {
			result = (String) actResult;
		} else {
			result = actResult.toString();
		}

		// URL�ƃN�G����������m�F
		category.debug("method:[" + method + " = (" + result + ")]     " + request.getMethod() + " ---> " + fullUrl);

		// TODO �Ō�ɍ폜����\��
		System.out.println("method:[" + method + " = (" + result + ")]     " + request.getMethod() + " ---> " + fullUrl);
		return fullUrl;
	}
}

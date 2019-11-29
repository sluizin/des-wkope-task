package des.wangku.operate.standard.webserver.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import des.wangku.operate.standard.utls.UtilsValidateCode;
import des.wangku.operate.standard.webserver.Consts;

/**
 * 
 * 验证码控制器<br>
 * /MQ/Verification/getVeripict_100_40_5_160<br>
 * /MQ/Verification/getVeribase64_w100h40c5l160<br>
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/MQ/Verification")
public class ValidateCode {

	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(ValidateCode.class);
	@Autowired
	HttpServletRequest request;

	/**
	 * 得到验证码的图片数据流
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param width int
	 * @param height int
	 * @param codeCount int
	 * @param lineCount int
	 * @return Object
	 */
	@ResponseBody
	@RequestMapping(value = { "/getVeripict_{width:[\\d]+}_{height:[\\d]+}_{codeCount:[\\d]+}_{lineCount:[\\d]+}" },
	produces = MediaType.APPLICATION_OCTET_STREAM_VALUE +";charset=utf-8"
	)
	public Object toMakeVC_Picture(HttpServletRequest request, 
			HttpServletResponse response, 
			@PathVariable int width,
			@PathVariable int height,
			@PathVariable int codeCount,
			@PathVariable int lineCount
			) {
		UtilsValidateCode vCode = new UtilsValidateCode(width, height, codeCount, lineCount);
		String value=vCode.getCode();
		byte[] bytes=vCode.getByteArray();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDispositionFormData("attachment", value + ".png");
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);	
		//headers.setContentType(MediaType.IMAGE_PNG);
		return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
	}
	/**
	 * 得到验证码的base64字符串
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param width int
	 * @param height int
	 * @param codeCount int
	 * @param lineCount int
	 * @return Object
	 */
	@ResponseBody
	@RequestMapping(value = { "/getVeribase64_w{width:[\\d]+}h{height:[\\d]+}c{codeCount:[\\d]+}l{lineCount:[\\d]+}" },
	produces = MediaType.TEXT_PLAIN_VALUE +";charset=utf-8"
	)
	public Object toMakeVC_Base64(HttpServletRequest request, 
			HttpServletResponse response,
			@PathVariable int width,
			@PathVariable int height,
			@PathVariable int codeCount,
			@PathVariable int lineCount
			) {
		UtilsValidateCode vCode = new UtilsValidateCode(width, height, codeCount, lineCount);
		String code=vCode.getCode();
		String val=code+"|"+vCode.toBase64();
		request.getSession().setAttribute(Consts.ACC_VeriCodeSessionKey,code);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);	
		return val;
	}
	@ResponseBody
	@RequestMapping(value = { "/getVeriCheck" },
	produces = MediaType.TEXT_PLAIN_VALUE +";charset=utf-8"
	)
	public Object toMakeVC_check2(HttpServletRequest request, 
			HttpServletResponse response,
			@RequestParam(value="vericode",required=true) String vericode
			) {
		return toMakeVC_check(request,response,vericode);
	}
	@ResponseBody
	@RequestMapping(value = { "/getVeriCheck_{code}" },
	produces = MediaType.TEXT_PLAIN_VALUE +";charset=utf-8"
	)
	public Object toMakeVC_check(HttpServletRequest request, 
			HttpServletResponse response,
			@PathVariable String code
			) {
		if(code==null)return "no";
		Object obj=request.getSession().getAttribute(Consts.ACC_VeriCodeSessionKey);
		if(obj==null)return "no";
		String val=obj.toString();
		System.out.println("SessionValue:"+val);
		if(code.equals(val))return "yes";
		return "no";
	}
}

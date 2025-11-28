package egovframework.let.cop.bbs.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.SessionVO;
import egovframework.com.cmm.service.EgovCmmUseService;
import egovframework.com.cmm.service.EgovFileMngService;
import egovframework.com.cmm.service.EgovFileMngUtil;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.com.cmm.util.MediaUtils;
import egovframework.let.cop.bbs.service.BoardMaster;
import egovframework.let.cop.bbs.service.BoardMasterVO;
import egovframework.let.cop.bbs.service.BoardSapFileVO;
import egovframework.let.cop.bbs.service.EgovBBSAttributeManageService;

import egovframework.rte.fdl.cmmn.exception.EgovBizException;
import egovframework.rte.fdl.property.EgovPropertyService;

import javax.annotation.Resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 게시판 속성관리를 위한 컨트롤러  클래스
 * @author 공통 서비스 개발팀 이삼섭
 * @since 2009.03.12
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      수정자          수정내용
 *  -------    --------    ---------------------------
 *  2009.03.12  이삼섭          최초 생성
 *  2009.06.26	한성곤		2단계 기능 추가 (댓글관리, 만족도조사)
 *  2011.08.31  JJY            경량환경 템플릿 커스터마이징버전 생성
 *
 *  </pre>
 */
@Controller
public class EgovBBSAttributeManageController {

	Logger log = Logger.getLogger(this.getClass());

	/** EgovBBSAttributeManageService */
    @Resource(name = "EgovBBSAttributeManageService")
    private EgovBBSAttributeManageService bbsAttrbService;
    

    /** EgovCmmUseService */
    @Resource(name = "EgovCmmUseService")
    private EgovCmmUseService cmmUseService;

    /** EgovPropertyService */
    @Resource(name = "propertiesService")
    protected EgovPropertyService propertyService;

    /** EgovMessageSource */
    @Resource(name="egovMessageSource")
    EgovMessageSource egovMessageSource;
    
    @Resource(name="EgovFileMngService")
    private EgovFileMngService fileMngService;
   

    @Resource(name="EgovFileMngUtil")
    private EgovFileMngUtil fileUtil;


    /**
     * 운영자 권한을 확인한다.(로그인 여부를 확인한다.)
     *
     * @param boardMaster
     * @throws EgovBizException
     */
    protected boolean checkAuthority(ModelMap model) throws Exception {
    	// 사용자권한 처리
    	if(!EgovUserDetailsHelper.isAuthenticated()) {
    		model.addAttribute("message", egovMessageSource.getMessage("fail.common.login"));
        	return false;
    	}else{
    		return true;
    	}
    }
    
    /**
     *RPA 목록을 수정한다 kkk.
     */
    @RequestMapping("/cop/bbs/UpdateBBSMasterInf.do")
    //public String updateBBSMasterInf(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO,  ModelMap model) throws Exception {
    public String updateBBSMasterInf(
    		//@RequestParam("confirmYn") String confirmYn ,
    		//@RequestParam("searchBLNo") String searchBLNo ,
    		@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, 
    		@ModelAttribute("boardMaster") BoardMaster boardMaster,
    	    BindingResult bindingResult, ModelMap model) throws Exception {
	
    	if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
    	
    	
    	System.out.println("all  ==>"+ boardMaster.toString());
    	System.out.println("확정구분  ==>"+ boardMaster.getConfirmYn());
    	System.out.println("bl번호 ==>"+boardMaster.getSearchBlNo());
    	
		bbsAttrbService.updateBBSMasterInf(boardMaster);
		
		System.out.println("what");
		
		if(boardMaster.getConfirmYn().equals("Y") || boardMaster.getConfirmYn().equals("S") || boardMaster.getConfirmYn().equals("F")) {
			//bbsAttrbService.insertBBSMasterSAPInf(boardMaster);
			System.out.println("엑셀화면");
			//return "cop/bbs/SelectBBSMasterInfsPop";
			return "cop/bbs/EgovBoardMstrListPop";
		}else {
			//bbsAttrbService.deleteBBSMasterSAPInf(boardMaster);
			System.out.println("리플레쉬");
			
			//return "cop/bbs/EgovBoardMstrList";
			//bbsAttrbService.selectBBSMasterInfs(boardMasterVO);
			return "forward:/cop/bbs/SelectBBSMasterInfs.do";
		}
    }
    
    /**
     *RPA 목록을 수정한다 kkk.
     */
    @RequestMapping("/cop/bbs/popupBBSMasterInf.do")
    //public String updateBBSMasterInf(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO,  ModelMap model) throws Exception {
    public String popupBBSMasterInf(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
	
    	System.out.println("확정구분"+ boardMasterVO.getSearchCnd());
    	System.out.println("시작일"+boardMasterVO.getSearchBgnDe());
    	System.out.println("종료일"+boardMasterVO.getSearchEndDe());
    	System.out.println("bl번호"+boardMasterVO.getSearchBl());
		
		return "forward:/cop/bbs/SelectBBSMasterInfs.do";
    }
    
    /**
     *RPA 목록을 수정한다 kkk.
     */
    @RequestMapping(value="/cop/bbs/FinishBBSMasterInf.do", method = {RequestMethod.GET, RequestMethod.POST})
    //public String updateBBSMasterInf(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO,  ModelMap model) throws Exception {
    public String finishBBSMasterInf(
    		@ModelAttribute("searchVO") BoardMasterVO boardMasterVO,
    		@ModelAttribute("boardMaster") BoardMaster boardMaster,
    	    BindingResult bindingResult, ModelMap model) throws Exception {
	
    	String  t3_confirm = boardMasterVO.gett3_confirm();
    	
    	if (t3_confirm.equals("T")) {
    		/*
    		String  checkContType = boardMaster.getCheckContType();
    		
    		String[] words = checkContType.split(",");
    		List<String> nameVOList = new ArrayList<String>();
            for (String wo : words ){
            	nameVOList.add(wo);
            }
            
            boardMaster.setNameVOList(nameVOList);
            
            //bbsAttrbService.deleteBBSMasterSAPInf(boardMaster);
            bbsAttrbService.insertBBSMasterSAPInf(boardMaster, boardMasterVO);
           // bbsAttrbService.finishBBSMasterInf(boardMasterVO);
            */
            boardMasterVO.setProcessflag("REG");
        	Map<String, Object> map = bbsAttrbService.selectSapSubBLSPList(boardMasterVO);    	
        	
    		model.addAttribute("resultList", map.get("resultList"));
    		model.addAttribute("searchBgnDe", boardMasterVO.getSearchBgnDe());
    		model.addAttribute("searchEndDe", boardMasterVO.getSearchEndDe());
    		model.addAttribute("searchBl", boardMasterVO.getSearchBl());
    		model.addAttribute("t3_confirm",  map.get("t3_confirm"));
    		model.addAttribute("t3_preconfirm",  map.get("t3_preconfirm"));

    		
    		bbsAttrbService.finishBBSMasterInf(boardMasterVO);
    	}
    	else {
    		boardMasterVO.setProcessflag("FISISH");
        	Map<String, Object> map = bbsAttrbService.selectSapSubBLSPList(boardMasterVO);    	
        	
    		model.addAttribute("resultList", map.get("resultList"));
    		model.addAttribute("searchBgnDe", boardMasterVO.getSearchBgnDe());
    		model.addAttribute("searchEndDe", boardMasterVO.getSearchEndDe());
    		model.addAttribute("searchBl", boardMasterVO.getSearchBl());
    		model.addAttribute("t3_confirm",  map.get("t3_confirm"));
    		model.addAttribute("t3_preconfirm",  map.get("t3_preconfirm"));

    		
    		bbsAttrbService.finishBBSMasterInf(boardMasterVO);
    	}
		
    	return "cop/bbs/EgovBoardSapSubList";
        //return "forward:/cop/bbs/SelectBBSSapSubInfs.do";
    }
    
    /**
     *RPA 목록을 조회한다.
     */
    @RequestMapping("/cop/bbs/SelectBBSMasterInfs.do")
    public String selectBBSMasterInfs(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
    	
    	if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
    	
    	System.out.println("확정구분"+ boardMasterVO.getSearchCnd());
    	System.out.println("시작일"+boardMasterVO.getSearchBgnDe());
    	System.out.println("종료일"+boardMasterVO.getSearchEndDe());
    	System.out.println("bl번호"+boardMasterVO.getSearchBl());
    	
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    	Calendar cal = Calendar.getInstance();
    	
    	String firstDate = boardMasterVO.getSearchBgnDe();
    	String lastDate = boardMasterVO.getSearchEndDe();
    	 
    	if ((boardMasterVO.getSearchBgnDe() == null) || (boardMasterVO.getSearchBgnDe().equals(""))) {
        	cal.set(Calendar.DATE, 1);
       	 
            firstDate = formatter.format(cal.getTime());
            
        	boardMasterVO.setSearchBgnDe(firstDate);
    	}
    	if ((boardMasterVO.getSearchEndDe() == null) || (boardMasterVO.getSearchEndDe().equals(""))) {
        	cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        	
        	lastDate = formatter.format(cal.getTime());
    		boardMasterVO.setSearchEndDe(lastDate);
    	}
    	
		Map<String, Object> map = bbsAttrbService.selectBBSMasterInfs(boardMasterVO);

		model.addAttribute("resultList", map.get("resultList"));

		return "cop/bbs/EgovBoardMstrList";
    }

    /**
     * 게시판 마스터 상세내용을 조회한다.
     *
     * @param boardMasterVO
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping("/cop/bbs/SelectBBSMasterInf.do")
    public String selectBBSMasterInf(@ModelAttribute("searchVO") BoardMasterVO searchVO, ModelMap model) throws Exception {

    	if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
    	
    	//BoardMasterVO vo = BBSAttributeManageDAO.SelectInvoiceList(searchVO);
    	//model.addAttribute("result", vo);

		return "cop/bbs/EgovBoardMstrUpdt";
    }
    /**
     * 게시판 마스터 선택 팝업을 위한 목록을 조회한다.
     *
     * @param boardMasterVO
     * @param model
     * @return
     * @throws Exception
     */
	//    @RequestMapping("/cop/bbs/SelectBBSMasterInfsPop.do") KKK
	//    public String selectBBSMasterInfsPop(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
	@RequestMapping("/cop/bbs/SelectBBSMasterInfsPop.do")
	public String selectBBSMasterInfsPop(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
		if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
		
		System.out.println("all  ==>"+ boardMasterVO.toString());
    	System.out.println("확정구분  ==>"+ boardMasterVO.getConfirmYn());
    	System.out.println("bl번호 ==>"+boardMasterVO.getSearchBlNo());
    	
		return "cop/bbs/EgovBoardMstrListPop";
		                
	}
	
	/**
     * 게시판 마스터 선택 팝업을 위한 목록을 조회한다.
     *
     * @param boardMasterVO
     * @param model
     * @return
     * @throws Exception
     */
	//    @RequestMapping("/cop/bbs/SelectBBSMasterInfsPop.do") KKK
	//    public String selectBBSMasterInfsPop(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
	@RequestMapping(value="/cop/bbs/SelectBBSMasterImgPop.do", method = {RequestMethod.GET, RequestMethod.POST})
	public String selectBBSMasterImgPop(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
		if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
		
		System.out.println("all  ==>"+ boardMasterVO.toString());
    	System.out.println("확정구분  ==>"+ boardMasterVO.getConfirmYn());
    	System.out.println("bl번호 ==>"+boardMasterVO.getSearchBlNo());
    	System.out.println("파일구분 ==>"+boardMasterVO.getSearchFileCn());
    	
    	model.addAttribute("searchBlNo", boardMasterVO.getSearchBlNo());
    	model.addAttribute("searchFileCn", boardMasterVO.getSearchFileCn());
    	
		return "cop/bbs/EgovBoardSapFile";
		                
	}
	
	/**
     * 게시판 마스터 선택 팝업을 위한 목록을 조회한다.
     *
     * @param boardMasterVO
     * @param model
     * @return
     * @throws Exception
     */
	//    @RequestMapping("/cop/bbs/SelectBBSMasterInfsPop.do") KKK
	//    public String selectBBSMasterInfsPop(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
	@RequestMapping(value="/cop/bbs/SelectBBSFileUploadPop.do", method = {RequestMethod.GET, RequestMethod.POST})
	public String SelectBBSFileUploadPop(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
		if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
		
		System.out.println("all  ==>"+ boardMasterVO.toString());
    	System.out.println("확정구분  ==>"+ boardMasterVO.getConfirmYn());
    	System.out.println("bl번호 ==>"+boardMasterVO.getSearchBlNo());
    	System.out.println("파일구분 ==>"+boardMasterVO.getSearchFileCn());
    	
    	model.addAttribute("searchBlNo", boardMasterVO.getSearchBlNo());
    	model.addAttribute("searchFileCn", boardMasterVO.getSearchFileCn());
    	
		return "cop/bbs/popupFileUpload";
		                
	}
 

                          
	@RequestMapping("/cop/bbs/SelectInvoiceList.do")
	public void selectInvoiceList(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model, HttpServletResponse response) throws Exception {

	
		//if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
		
		System.out.println("bl번호"+boardMasterVO.getSearchBlNo());
		List<BoardMasterVO> list = bbsAttrbService.selectInvoiceList(boardMasterVO);
		
		//Excel Down 시작
		HSSFWorkbook workbook = new HSSFWorkbook();
		
		//시트생성
		HSSFSheet sheet = workbook.createSheet("Invoice");
		
		//해더부분셀에 스타일을 주기위한 인스턴스 생성   
	    HSSFCellStyle cellStyle = workbook.createCellStyle();            
	    cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);                     //스타일인스턴스의 속성 셑팅           
	    cellStyle.setFillForegroundColor(HSSFCellStyle.BORDER_DASH_DOT);        //셀에 색깔 채우기   
	    cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);   
	    cellStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);              //테두리 설정   
	    cellStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);   
	    cellStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);   
	    cellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);   
	    HSSFFont font = workbook.createFont();                                    //폰트 조정 인스턴스 생성   
	    font.setBoldweight((short)700);        
	    cellStyle.setFont(font);
	    
	  //얇은 테두리를 위한 스타일 인스턴스 생성   
	    HSSFCellStyle cellStyle1 = workbook.createCellStyle();           
	    cellStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);   
	    cellStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);   
	    cellStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);   
	    cellStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);   
	
	
		
		//행, 열, 열번호
		HSSFRow row = null;
		HSSFCell cell = null;
	    
		
		int rowCount = 0;
	    int cellCount = 0;
		
		// 헤더 생성
	    row = sheet.createRow(rowCount++);
	    
	    cell = row.createCell(0);
	    cell.setCellValue("INVOICD_No.");
	    sheet.setColumnWidth(0, 4000);
	    
	    cell = row.createCell(1);
	    cell.setCellValue("운송구분");
	    sheet.setColumnWidth(1, 4000);
	    
	    cell = row.createCell(2);
	    cell.setCellValue("서류송부일");
	    sheet.setColumnWidth(2, 4000);
	    
	    
	    cell = row.createCell(3);
	    cell.setCellValue("PO No.");
	    sheet.setColumnWidth(3, 4000);
	    
	    cell = row.createCell(4);
	    cell.setCellValue("품목 No. (발주항번)");
	    sheet.setColumnWidth(4, 5000);
	    
	    cell = row.createCell(5);
	    cell.setCellValue("자재코드 (품목코드)");
	    sheet.setColumnWidth(5, 5000);
	    
	    cell = row.createCell(6);
	    cell.setCellValue("입고수량");
	    sheet.setColumnWidth(6, 5000);
	    
	    cell = row.createCell(7);
	    cell.setCellValue("단가");
	    sheet.setColumnWidth(7, 4000);
	    
	    cell = row.createCell(8);
	    cell.setCellValue("가격단위");
	    sheet.setColumnWidth(8, 4000);
	    
	    cell = row.createCell(9);
	    cell.setCellValue("입고금액");
	    sheet.setColumnWidth(9, 4000);
	    
	    cell = row.createCell(10);
	    cell.setCellValue("화폐단위");
	    sheet.setColumnWidth(10, 4000);
	    
	    cell = row.createCell(11);
	    cell.setCellValue("HS 코드");
	    sheet.setColumnWidth(11, 4000);
	    
	    cell = row.createCell(12);
	    cell.setCellValue("포워드사");
	    sheet.setColumnWidth(12, 4000);
	    
	    cell = row.createCell(13);
	    cell.setCellValue("HOUSE B/L");
	    sheet.setColumnWidth(13, 4000);
	    
	    cell = row.createCell(14);
	    cell.setCellValue("인도조건");
	    sheet.setColumnWidth(14, 4000);
	    
	    
	    for(BoardMasterVO vo : list){
	    	row = sheet.createRow(rowCount++);
	    	System.out.println("row값" + row);
	    	
	    	cellCount = 0;
	        row.createCell(cellCount++).setCellValue(vo.gett1InNo());
	        row.createCell(cellCount++).setCellValue(vo.gett1Carry());
	        row.createCell(cellCount++).setCellValue(vo.gett3SSDt());
	        
	        row.createCell(cellCount++).setCellValue(vo.gett1PoNo());
	        row.createCell(cellCount++).setCellValue(vo.gett1PoLineNo());
	        row.createCell(cellCount++).setCellValue(vo.gett1ItemCd());
	        row.createCell(cellCount++).setCellValue(vo.gett1Qty());
	        
	        //cell.getNumericCellValue();
	        row.createCell(cellCount++).setCellValue(vo.gett4DanGa());
	        row.createCell(cellCount++).setCellValue(vo.gett4DanGa2());
	        row.createCell(cellCount++).setCellValue(vo.gett4GeumAeg());
	        row.createCell(cellCount++).setCellValue(vo.gett4JeCurDw());
	        row.createCell(cellCount++).setCellValue(vo.gett4SebeonCd());
	        
	        row.createCell(cellCount++).setCellValue(vo.gett3GgCoCd2());
	        row.createCell(cellCount++).setCellValue(vo.gett3HBlNo());
	        row.createCell(cellCount++).setCellValue(vo.gett4IndoJk());
	        
	    }
	    
	    String fileName ="수입신고.xls";
	    fileName  = URLEncoder.encode(fileName,"UTF-8");

	    
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("application/vnd.ms-excel;charset=EUC-KR");
	    response.setHeader("Pragma", "public");
	    response.setHeader("Expires", "0");
	    response.setHeader("Content-Disposition", "attachment; filename=" +fileName);
	    
	    OutputStream os = null;
	   
	   try {
	       //workbook = (HSSFWorkbook) model.get("workbook");
	       os = response.getOutputStream();
	       
	       // 파일생성
	       workbook.write(os);
	   }catch (Exception e) {
	       e.printStackTrace();
	   } finally {
	       if(workbook != null) {
	           try {
	           //    workbook.close();
	           os.close();	   
	           } catch (Exception e) {
	               e.printStackTrace();
	           }
	       }
	       
	       if(os != null) {
	           try {
	               os.close();
	           } catch (Exception e) {
	               e.printStackTrace();
	           }
	       }
	   }
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@RequestMapping("/cop/bbs/SelectCargoList.do")
	public void selectCargoList(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model, HttpServletResponse response) throws Exception {

	
		//if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
		
		List<BoardMasterVO> list = bbsAttrbService.selectCargoList(boardMasterVO);
		
		//Excel Down 시작
		HSSFWorkbook workbook = new HSSFWorkbook();
		
		//시트생성
		HSSFSheet sheet = workbook.createSheet("Cargo");
		
		
		//해더부분셀에 스타일을 주기위한 인스턴스 생성   
	    HSSFCellStyle cellStyle = workbook.createCellStyle();            
	    cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);                     //스타일인스턴스의 속성 셑팅           
	    cellStyle.setFillForegroundColor(HSSFCellStyle.BORDER_DASH_DOT);        //셀에 색깔 채우기   
	    cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);   
	    cellStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);              //테두리 설정   
	    cellStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);   
	    cellStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);   
	    cellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);   
	    HSSFFont font = workbook.createFont();                                    //폰트 조정 인스턴스 생성   
	    font.setBoldweight((short)700);        
	    cellStyle.setFont(font);
	    
	  //얇은 테두리를 위한 스타일 인스턴스 생성   
	    HSSFCellStyle cellStyle1 = workbook.createCellStyle();           
	    cellStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);   
	    cellStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);   
	    cellStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);   
	    cellStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);   
	
	
		
		//행, 열, 열번호
		HSSFRow row = null;
		HSSFCell cell = null;
	    
		
		int rowCount = 0;
	    int cellCount = 0;
		
		// 헤더 생성
	    row = sheet.createRow(rowCount++);
	    
	    cell = row.createCell(0);
	    cell.setCellValue("B/L일자");
	    sheet.setColumnWidth(0, 4000);
	    
	    cell = row.createCell(1);
	    cell.setCellValue("부보일자");
	    sheet.setColumnWidth(1, 4000);
	    
	    cell = row.createCell(2);
	    cell.setCellValue("보험관리번호");
	    sheet.setColumnWidth(2, 4000);
	    
	    
	    cell = row.createCell(3);
	    cell.setCellValue("부보요율");
	    sheet.setColumnWidth(3, 4000);
	    
	    cell = row.createCell(4);
	    cell.setCellValue("적용환율");
	    sheet.setColumnWidth(4, 4000);
	    
	    cell = row.createCell(5);
	    cell.setCellValue("통화단위");
	    sheet.setColumnWidth(5, 4000);
	    
	    cell = row.createCell(6);
	    cell.setCellValue("증권발급일자");
	    sheet.setColumnWidth(6, 4000);
	    
	    cell = row.createCell(7);
	    cell.setCellValue("보험료");
	    sheet.setColumnWidth(7, 4000);
	    
	    cell = row.createCell(8);
	    cell.setCellValue("INV");
	    sheet.setColumnWidth(8, 4000);
	    
	    cell = row.createCell(9);
	    cell.setCellValue("HOUSE B/L");
	    sheet.setColumnWidth(9, 4000);
	    
	    
	    
	    for(BoardMasterVO vo : list){
	    	row = sheet.createRow(rowCount++);
	    	System.out.println("row값" + row);
	    	
	    	cellCount = 0;
	        row.createCell(cellCount++).setCellValue(vo.gett3BlDt());
	        row.createCell(cellCount++).setCellValue(vo.gett3SSDt());
	        row.createCell(cellCount++).setCellValue(vo.gett2StockNo());
	        
	        row.createCell(cellCount++).setCellValue(vo.gett2JyRt());
	        row.createCell(cellCount++).setCellValue(vo.gett4AACUERt());
	        row.createCell(cellCount++).setCellValue(vo.gett4AACDw());
	        row.createCell(cellCount++).setCellValue(vo.gett3SSDt());
	        
	        //cell.getNumericCellValue();
	        row.createCell(cellCount++).setCellValue(vo.gett2CKEIFee());
	        row.createCell(cellCount++).setCellValue(vo.gett3InNo());
	        row.createCell(cellCount++).setCellValue(vo.gett3HBlNo());   
	    }
	 
	    String fileName ="적하보험.xls";
	    fileName  = URLEncoder.encode(fileName,"UTF-8");

	    
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("application/vnd.ms-excel;charset=EUC-KR");
	    response.setHeader("Pragma", "public");
	    response.setHeader("Expires", "0");
	    response.setHeader("Content-Disposition", "attachment; filename=" +fileName);
	    
	    OutputStream os = null;

	   try {
	       //workbook = (HSSFWorkbook) model.get("workbook");
	       os = response.getOutputStream();
	       
	       // 파일생성
	       workbook.write(os);
	   }catch (Exception e) {
	       e.printStackTrace();
	   } finally {
	       if(workbook != null) {
	           try {
	//               workbook.close();
	           } catch (Exception e) {
	               e.printStackTrace();
	           }
	       }
	       
	       if(os != null) {
	           try {
	               os.close();
	           } catch (Exception e) {
	               e.printStackTrace();
	           }
	       }
	   }
	}
	
	
	
	@RequestMapping("/cop/bbs/SelectBlList.do")
	public void selectBlList(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model, HttpServletResponse response) throws Exception {

	
		//if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
		
		List<BoardMasterVO> list = bbsAttrbService.selectBlList(boardMasterVO);
		
		//Excel Down 시작
		HSSFWorkbook workbook = new HSSFWorkbook();
		
		//시트생성
		HSSFSheet sheet = workbook.createSheet("Cargo");
		
		
		//해더부분셀에 스타일을 주기위한 인스턴스 생성   
	    HSSFCellStyle cellStyle = workbook.createCellStyle();            
	    cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);                     //스타일인스턴스의 속성 셑팅           
	    cellStyle.setFillForegroundColor(HSSFCellStyle.BORDER_DASH_DOT);        //셀에 색깔 채우기   
	    cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);   
	    cellStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);              //테두리 설정   
	    cellStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);   
	    cellStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);   
	    cellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);   
	    HSSFFont font = workbook.createFont();                                    //폰트 조정 인스턴스 생성   
	    font.setBoldweight((short)700);        
	    cellStyle.setFont(font);
	    
	  //얇은 테두리를 위한 스타일 인스턴스 생성   
	    HSSFCellStyle cellStyle1 = workbook.createCellStyle();           
	    cellStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);   
	    cellStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);   
	    cellStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);   
	    cellStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);   
	
	
		
		//행, 열, 열번호
		HSSFRow row = null;
		HSSFCell cell = null;
	    
		
		int rowCount = 0;
	    int cellCount = 0;
		
		// 헤더 생성
	    row = sheet.createRow(rowCount++);
	    
	    cell = row.createCell(0);
	    cell.setCellValue("HOUSE B/L");
	    sheet.setColumnWidth(0, 4000);
	    
	    cell = row.createCell(1);
	    cell.setCellValue("B/L선적일자");
	    sheet.setColumnWidth(1, 4000);
	    
	    cell = row.createCell(2);
	    cell.setCellValue("ETD(출항예정일");
	    sheet.setColumnWidth(2, 4000);
	    
	    
	    cell = row.createCell(3);
	    cell.setCellValue("ETA (입항예정일)");
	    sheet.setColumnWidth(3, 4000);
	    
	    cell = row.createCell(4);
	    cell.setCellValue("선적국");
	    sheet.setColumnWidth(4, 4000);
	    
	    cell = row.createCell(5);
	    cell.setCellValue("도착국");
	    sheet.setColumnWidth(5, 4000);
	    
	    cell = row.createCell(6);
	    cell.setCellValue("선적항");
	    sheet.setColumnWidth(6, 4000);
	    
	    cell = row.createCell(7);
	    cell.setCellValue("도착항");
	    sheet.setColumnWidth(7, 4000);
	    
	    cell = row.createCell(8);
	    cell.setCellValue("FLT / VSSL (편명 및 선명)");
	    sheet.setColumnWidth(8, 4000);
	    
	    cell = row.createCell(9);
	    cell.setCellValue("20FT Qty");
	    sheet.setColumnWidth(9, 4000);
	    
	    cell = row.createCell(10);
	    cell.setCellValue("40FT Qty");
	    sheet.setColumnWidth(10, 4000);
	    
	    
	    
	    
	    
	    
	    cell = row.createCell(11);
	    cell.setCellValue("운송구분");
	    sheet.setColumnWidth(11, 4000);
	    
	    cell = row.createCell(12);
	    cell.setCellValue("총포장갯수");
	    sheet.setColumnWidth(12, 4000);
	    
	    cell = row.createCell(13);
	    cell.setCellValue("순중량/Net Weight");
	    sheet.setColumnWidth(13, 4000);
	    
	    cell = row.createCell(14);
	    cell.setCellValue("총중량/Gross Weight");
	    sheet.setColumnWidth(14, 4000);
	    
	    cell = row.createCell(15);
	    cell.setCellValue("서류송부일");
	    sheet.setColumnWidth(15, 4000);
	    
	    cell = row.createCell(16);
	    cell.setCellValue("선적서류 송부처");
	    sheet.setColumnWidth(16, 4000);
	    
	    cell = row.createCell(17);
	    cell.setCellValue("실입항일");
	    sheet.setColumnWidth(17, 4000);
	    
	    cell = row.createCell(18);
	    cell.setCellValue("입고예정일");
	    sheet.setColumnWidth(18, 4000);
	    
	    
	    
	    
	    for(BoardMasterVO vo : list){
	    	row = sheet.createRow(rowCount++);
	    	System.out.println("row값" + row);
	    	
	    	cellCount = 0;
	        row.createCell(cellCount++).setCellValue(vo.gett3HblNo());
	        row.createCell(cellCount++).setCellValue(vo.gett3BSDt());
	        row.createCell(cellCount++).setCellValue(vo.gett3EtaDt());
	        
	        row.createCell(cellCount++).setCellValue(vo.gett3EtaDt());
	        row.createCell(cellCount++).setCellValue(vo.gett3SJGCd());
	        row.createCell(cellCount++).setCellValue(vo.gett3DJGCd());
	        row.createCell(cellCount++).setCellValue(vo.gett3SJHCd());
	        
	        //cell.getNumericCellValue();
	        row.createCell(cellCount++).setCellValue(vo.gett3DCHCd());
	        row.createCell(cellCount++).setCellValue(vo.gett3FVNm());
	        row.createCell(cellCount++).setCellValue(vo.gett3C20Qt());   
	    
	        row.createCell(cellCount++).setCellValue(vo.gett3C40Qt());
	        row.createCell(cellCount++).setCellValue(vo.gett3USgb());
	        row.createCell(cellCount++).setCellValue(vo.gett3CPJEa());   
	        
	        row.createCell(cellCount++).setCellValue(vo.gett3NWg());
	        row.createCell(cellCount++).setCellValue(vo.gett3GWg());
	        row.createCell(cellCount++).setCellValue(vo.gett3SLSBDt());   
	    
	        row.createCell(cellCount++).setCellValue(vo.gett3SJSLSBCCd());
	        row.createCell(cellCount++).setCellValue(vo.gett3SIHDt());
	        row.createCell(cellCount++).setCellValue(vo.gett3IGYJDt());   
	    
	    
	    
	    
	    
	    }
	 
	    String fileName ="BL.xls";
	    fileName  = URLEncoder.encode(fileName,"UTF-8");
	    
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("application/vnd.ms-excel;charset=EUC-KR");
	    response.setHeader("Pragma", "public");
	    response.setHeader("Expires", "0");
	    response.setHeader("Content-Disposition", "attachment; filename=" +fileName);
	   OutputStream os = null;
	   //HSSFWorkbook workbook = null;
	   
	   try {
	       //workbook = (HSSFWorkbook) model.get("workbook");
	       os = response.getOutputStream();
	       
	       // 파일생성
	       workbook.write(os);
	   }catch (Exception e) {
	       e.printStackTrace();
	   } finally {
	       if(workbook != null) {
	           try {
	//               workbook.close();
	           } catch (Exception e) {
	               e.printStackTrace();
	           }
	       }
	       
	       if(os != null) {
	           try {
	               os.close();
	           } catch (Exception e) {
	               e.printStackTrace();
	           }
	       }
	   }
	}
	
	
	
	
	@RequestMapping("/cop/bbs/SelectPassList.do")
	public void selectPassList(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model, HttpServletResponse response) throws Exception {

	
		//if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
		
		List<BoardMasterVO> list = bbsAttrbService.selectPassList(boardMasterVO);
		
		//Excel Down 시작
		HSSFWorkbook workbook = new HSSFWorkbook();
		
		//시트생성
		HSSFSheet sheet = workbook.createSheet("Cargo");
		
		
		//해더부분셀에 스타일을 주기위한 인스턴스 생성   
	    HSSFCellStyle cellStyle = workbook.createCellStyle();            
	    cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);                     //스타일인스턴스의 속성 셑팅           
	    cellStyle.setFillForegroundColor(HSSFCellStyle.BORDER_DASH_DOT);        //셀에 색깔 채우기   
	    cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);   
	    cellStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);              //테두리 설정   
	    cellStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);   
	    cellStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);   
	    cellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);   
	    HSSFFont font = workbook.createFont();                                    //폰트 조정 인스턴스 생성   
	    font.setBoldweight((short)700);        
	    cellStyle.setFont(font);
	    
	  //얇은 테두리를 위한 스타일 인스턴스 생성   
	    HSSFCellStyle cellStyle1 = workbook.createCellStyle();           
	    cellStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);   
	    cellStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);   
	    cellStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);   
	    cellStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);   
	
	
		
		//행, 열, 열번호
		HSSFRow row = null;
		HSSFCell cell = null;
	    
		
		int rowCount = 0;
	    int cellCount = 0;
		
		// 헤더 생성
	    row = sheet.createRow(rowCount++);
	    
	    cell = row.createCell(0);
	    cell.setCellValue("HOUSE B/L");
	    sheet.setColumnWidth(0, 4000);
	    
	    cell = row.createCell(1);
	    cell.setCellValue("통관요청일");
	    sheet.setColumnWidth(1, 4000);
	    
	    cell = row.createCell(2);
	    cell.setCellValue("신고자");
	    sheet.setColumnWidth(2, 4000);
	    
	    
	    cell = row.createCell(3);
	    cell.setCellValue("신고번호");
	    sheet.setColumnWidth(3, 4000);
	    
	    cell = row.createCell(4);
	    cell.setCellValue("세관.과-징수");
	    sheet.setColumnWidth(4, 4000);
	    
	    cell = row.createCell(5);
	    cell.setCellValue("신고수리일");
	    sheet.setColumnWidth(5, 4000);
	    
	    cell = row.createCell(6);
	    cell.setCellValue("BL 사항");
	    sheet.setColumnWidth(6, 4000);
	    
	    cell = row.createCell(7);
	    cell.setCellValue("총과세가격 $");
	    sheet.setColumnWidth(7, 4000);
	    
	    cell = row.createCell(8);
	    cell.setCellValue("총과세가격 원");
	    sheet.setColumnWidth(8, 4000);
	    
	    cell = row.createCell(9);
	    cell.setCellValue("부가가치세 과표");
	    sheet.setColumnWidth(9, 4000);
	    
	    
	    cell = row.createCell(10);
	    cell.setCellValue("운임");
	    sheet.setColumnWidth(10, 4000);
	    
	    cell = row.createCell(11);
	    cell.setCellValue("환율");
	    sheet.setColumnWidth(11, 4000);
	    
	    cell = row.createCell(12);
	    cell.setCellValue("가산금액");
	    sheet.setColumnWidth(12, 4000);
	    
	    cell = row.createCell(13);
	    cell.setCellValue("총관세");
	    sheet.setColumnWidth(13, 4000);
	    
	    cell = row.createCell(14);
	    cell.setCellValue("총부가세");
	    sheet.setColumnWidth(14, 4000);
	    
	    cell = row.createCell(15);
	    cell.setCellValue("수입신고필증");
	    sheet.setColumnWidth(15, 4000);
	    
	    cell = row.createCell(16);
	    cell.setCellValue("PO번호");
	    sheet.setColumnWidth(16, 4000);
	    
	    
	    
	    cell = row.createCell(17);
	    cell.setCellValue("품목코드");
	    sheet.setColumnWidth(17, 4000);
	    
	    
	    cell = row.createCell(18);
	    cell.setCellValue("란 정보");
	    sheet.setColumnWidth(18, 4000);
	    
	    
	    
	    cell = row.createCell(19);
	    cell.setCellValue("행 정보");
	    sheet.setColumnWidth(19, 4000);
	    
	    cell = row.createCell(20);
	    cell.setCellValue("관세율");
	    sheet.setColumnWidth(20, 4000);
	    
	    cell = row.createCell(21);
	    cell.setCellValue("관세");
	    sheet.setColumnWidth(21, 4000);
	    /* 2025-11-24 추가  (수입통관 집계) */
	    class TempTB {
	    	String t4LanNo24;
	    	String t4GSY25;
	    	String t4SJGSE26;
	    	
	    	TempTB(String t4LanNo24, String t4GSY25, String t4SJGSE26){
	    		this.t4LanNo24 = t4LanNo24;
	    		this.t4GSY25 = t4GSY25;
	    		this.t4SJGSE26 = t4SJGSE26;
	    	}
	    }
	    List<TempTB> tempTB = new ArrayList<>();
	    /* //2025-11-24 추가  (수입통관 집계) */
	    
	    for(BoardMasterVO vo : list){
	    	row = sheet.createRow(rowCount++);
	    	System.out.println("row값" + row);
	    	
	    	cellCount = 0;
	        row.createCell(cellCount++).setCellValue(vo.gett3HBlNo());
	        row.createCell(cellCount++).setCellValue(vo.gett4TgYcDt());//통관요청일
	        row.createCell(cellCount++).setCellValue(vo.gett4SinGoJa());//신고자
	        row.createCell(cellCount++).setCellValue(vo.gett4SingoNo());
	        row.createCell(cellCount++).setCellValue(vo.gett4SGSG());
	        row.createCell(cellCount++).setCellValue(vo.gett4SingoDt());
	        row.createCell(cellCount++).setCellValue("");//bl사항
	        row.createCell(cellCount++).setCellValue(vo.gett4CifUsd());
	        row.createCell(cellCount++).setCellValue(vo.gett4CifKrw());
	        row.createCell(cellCount++).setCellValue(vo.gett4BGSGP());
	        
	        
	        row.createCell(cellCount++).setCellValue("");//운임
	        row.createCell(cellCount++).setCellValue("");//환율
	        row.createCell(cellCount++).setCellValue("");//가산금액
	        row.createCell(cellCount++).setCellValue(vo.gett4GwanSe());
	        row.createCell(cellCount++).setCellValue(vo.gett4Bugase());
	        row.createCell(cellCount++).setCellValue("");//수입신고필증
	        row.createCell(cellCount++).setCellValue(vo.gett1PoNo());
	        row.createCell(cellCount++).setCellValue(vo.gett1PoLineNo());
	        row.createCell(cellCount++).setCellValue(vo.gett4LanNo2());   
	        row.createCell(cellCount++).setCellValue(vo.gett4HeangNo());
	        
	        row.createCell(cellCount++).setCellValue(vo.gett4GSY());
	        row.createCell(cellCount++).setCellValue(vo.gett4SJGSE());   
	        
		    /* 2025-11-24 추가  (수입통관 집계) */
	        int intT4HeangNo = Integer.parseInt(vo.gett4HeangNo());
	        if (intT4HeangNo == 1) {
	        	tempTB.add(new TempTB(vo.gett4LanNo2(), vo.gett4GSY(), vo.gett4SJGSE()));
	        }

		    /* //2025-11-24 추가  (수입통관 집계) */
	    }
	    

	    /* 2025-11-24 추가  (수입통관 집계) */
		rowCount = 1;
		for (TempTB t : tempTB) {
			if (cellCount > 20) {
				row = sheet.getRow(rowCount++);
			} else {
				row = sheet.createRow(rowCount++);
			}
	    	cellCount = 23;
			row.createCell(cellCount++).setCellValue(t.t4LanNo24);
			row.createCell(cellCount++).setCellValue(t.t4GSY25);
			row.createCell(cellCount++).setCellValue(t.t4SJGSE26);
        }
	    /* //2025-11-24 추가  (수입통관 집계) */
	    
	    String fileName ="수입통관.xls";
	    fileName  = URLEncoder.encode(fileName,"UTF-8");
	    
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("application/vnd.ms-excel;charset=EUC-KR");
	    response.setHeader("Pragma", "public");
	    response.setHeader("Expires", "0");
	    response.setHeader("Content-Disposition", "attachment; filename=" +fileName);
	    OutputStream os = null;
	   //HSSFWorkbook workbook = null;
	   
	   try {
	       //workbook = (HSSFWorkbook) model.get("workbook");
	       os = response.getOutputStream();
	       
	       // 파일생성
	       workbook.write(os);
	   }catch (Exception e) {
	       e.printStackTrace();
	   } finally {
	       if(workbook != null) {
	           try {
	         //      workbook.close();
	           } catch (Exception e) {
	               e.printStackTrace();
	           }
	       }
	       
	       if(os != null) {
	           try {
	               os.close();
	           } catch (Exception e) {
	               e.printStackTrace();
	           }
	       }
	   }
	}
 
	
	@RequestMapping("/cop/bbs/SelectDocList.do")
	public void selectDocList(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model, HttpServletResponse response) throws Exception {

	
		//if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
		
		List<BoardMasterVO> list = bbsAttrbService.selectDocList(boardMasterVO);
		
		//Excel Down 시작
		HSSFWorkbook workbook = new HSSFWorkbook();
		
		//시트생성
		HSSFSheet sheet = workbook.createSheet("Cargo");
		
		
		//해더부분셀에 스타일을 주기위한 인스턴스 생성   
	    HSSFCellStyle cellStyle = workbook.createCellStyle();            
	    cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);                     //스타일인스턴스의 속성 셑팅           
	    cellStyle.setFillForegroundColor(HSSFCellStyle.BORDER_DASH_DOT);        //셀에 색깔 채우기   
	    cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);   
	    cellStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);              //테두리 설정   
	    cellStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);   
	    cellStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);   
	    cellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);   
	    HSSFFont font = workbook.createFont();                                    //폰트 조정 인스턴스 생성   
	    font.setBoldweight((short)700);        
	    cellStyle.setFont(font);
	    
	  //얇은 테두리를 위한 스타일 인스턴스 생성   
	    HSSFCellStyle cellStyle1 = workbook.createCellStyle();           
	    cellStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);   
	    cellStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);   
	    cellStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);   
	    cellStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);   
	
	
		
		//행, 열, 열번호
		HSSFRow row = null;
		HSSFCell cell = null;
	    
		
		int rowCount = 0;
	    int cellCount = 0;
		
		// 헤더 생성
	    row = sheet.createRow(rowCount++);
	    
	    cell = row.createCell(0);
	    cell.setCellValue("B/L 관리번호");
	    sheet.setColumnWidth(0, 4000);
	    
	    cell = row.createCell(1);
	    cell.setCellValue("HOUSE B/L");
	    sheet.setColumnWidth(1, 4000);
	    
	    cell = row.createCell(2);
	    cell.setCellValue("PO번호"); /* 증빙일자 */
	    sheet.setColumnWidth(2, 4000);
	    
	    
	    cell = row.createCell(3);
	    cell.setCellValue("품목코드");  /* 전기일자 */
	    sheet.setColumnWidth(3, 4000);
	    
	    cell = row.createCell(4);
	    cell.setCellValue("물대");  /* 지급조건 */
	    sheet.setColumnWidth(4, 4000);
	    
	    
	    
	    
	    for(BoardMasterVO vo : list){
	    	row = sheet.createRow(rowCount++);
	    	System.out.println("row값" + row);
	    	
	    	cellCount = 0;
	        row.createCell(cellCount++).setCellValue(vo.gett4GwanliNo());
	        row.createCell(cellCount++).setCellValue(vo.gett3HBlNo());
	        row.createCell(cellCount++).setCellValue(vo.gett3JBDt());
	        row.createCell(cellCount++).setCellValue(vo.gett3JGDt());
	        row.createCell(cellCount++).setCellValue(vo.gett3JGJk());
	        
	    }
	 
	    String fileName ="송장생성.xls";
	    fileName  = URLEncoder.encode(fileName,"UTF-8");
	    
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("application/vnd.ms-excel;charset=EUC-KR");
	    response.setHeader("Pragma", "public");
	    response.setHeader("Expires", "0");
	    response.setHeader("Content-Disposition", "attachment; filename=" +fileName);
	
	    
	   OutputStream os = null;
	   //HSSFWorkbook workbook = null;
	   
	   try {
	       //workbook = (HSSFWorkbook) model.get("workbook");
	       os = response.getOutputStream();
	       
	       // 파일생성
	       workbook.write(os);
	   }catch (Exception e) {
	       e.printStackTrace();
	   } finally {
	       if(workbook != null) {
	           try {
	//               workbook.close();
	           } catch (Exception e) {
	               e.printStackTrace();
	           }
	       }
	       
	       if(os != null) {
	           try {
	               os.close();
	           } catch (Exception e) {
	               e.printStackTrace();
	           }
	       }
	   }
	}
	
	
	
	@RequestMapping("/cop/bbs/SelectSapList.do")
	public void selectSapList(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model, HttpServletResponse response) throws Exception {

	
		//if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
		
		List<BoardMasterVO> list = bbsAttrbService.selectSapList(boardMasterVO);
		
		//Excel Down 시작
		HSSFWorkbook workbook = new HSSFWorkbook();
		
		//시트생성
		HSSFSheet sheet = workbook.createSheet("Sap");
		
		
		//해더부분셀에 스타일을 주기위한 인스턴스 생성   
	    HSSFCellStyle cellStyle = workbook.createCellStyle();            
	    cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);                     //스타일인스턴스의 속성 셑팅           
	    cellStyle.setFillForegroundColor(HSSFCellStyle.BORDER_DASH_DOT);        //셀에 색깔 채우기   
	    cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);   
	    cellStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);              //테두리 설정   
	    cellStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);   
	    cellStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);   
	    cellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);   
	    HSSFFont font = workbook.createFont();                                    //폰트 조정 인스턴스 생성   
	    font.setBoldweight((short)700);        
	    cellStyle.setFont(font);
	    
	  //얇은 테두리를 위한 스타일 인스턴스 생성   
	    HSSFCellStyle cellStyle1 = workbook.createCellStyle();           
	    cellStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);   
	    cellStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);   
	    cellStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);   
	    cellStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);   
	
	
		
		//행, 열, 열번호
		HSSFRow row = null;
		HSSFCell cell = null;
	    
		
		int rowCount = 0;
	    int cellCount = 0;
		
		// 헤더 생성
	    row = sheet.createRow(rowCount++);
	    
	    cell = row.createCell(0);
	    cell.setCellValue("B/L 번호");
	    sheet.setColumnWidth(0, 4000);
	    
	    cell = row.createCell(1);
	    cell.setCellValue("수입문서번호");
	    sheet.setColumnWidth(1, 4000);
	    
	    cell = row.createCell(2);
	    cell.setCellValue("조건유형");
	    sheet.setColumnWidth(2, 4000);
	    
	    
	    cell = row.createCell(3);
	    cell.setCellValue("조건유형 텍스트");
	    sheet.setColumnWidth(3, 6000);
	    
	    cell = row.createCell(4);
	    cell.setCellValue("비용");
	    sheet.setColumnWidth(4, 4000);
	    
	    cell = row.createCell(5);
	    cell.setCellValue("통화");
	    sheet.setColumnWidth(5, 4000);
	    
	    cell = row.createCell(6);
	    cell.setCellValue("회계전표번호");
	    sheet.setColumnWidth(6, 4000);
	    
	    cell = row.createCell(7);
	    cell.setCellValue("전표번호");
	    sheet.setColumnWidth(7, 4000);
	    
	    cell = row.createCell(8);
	    cell.setCellValue("증빙일");
	    sheet.setColumnWidth(8, 4000);
	    
	    cell = row.createCell(9);
	    cell.setCellValue("전기일");
	    sheet.setColumnWidth(9, 4000);
	    
	    cell = row.createCell(10);
	    cell.setCellValue("공급업체");
	    sheet.setColumnWidth(10, 4000);
	    
	    cell = row.createCell(11);
	    cell.setCellValue("공급업체명");
	    sheet.setColumnWidth(11, 4000);
	    
	    cell = row.createCell(12);
	    cell.setCellValue("비용그룹");
	    sheet.setColumnWidth(12, 4000);
	    
	    cell = row.createCell(13);
	    cell.setCellValue("세액코드");
	    sheet.setColumnWidth(13, 4000);
	    
	    cell = row.createCell(14);
	    cell.setCellValue("세액");
	    sheet.setColumnWidth(14, 4000);
	    
	    cell = row.createCell(15);
	    cell.setCellValue("관리코드");
	    sheet.setColumnWidth(15, 4000);
	    
	    cell = row.createCell(16);
	    cell.setCellValue("지급조건");
	    sheet.setColumnWidth(16, 4000);
	    
	    
	    
	    
	    for(BoardMasterVO vo : list){
	    	row = sheet.createRow(rowCount++);
	    	System.out.println("row값" + row);
	    	
	    	cellCount = 0;
	        row.createCell(cellCount++).setCellValue(vo.getbktxt());
	        row.createCell(cellCount++).setCellValue(vo.getzfimdno());
	        row.createCell(cellCount++).setCellValue(vo.getcond_type());
	        row.createCell(cellCount++).setCellValue(vo.getvtext());
	        row.createCell(cellCount++).setCellValue(vo.getwrbtr());
	        
	        row.createCell(cellCount++).setCellValue(vo.getwaers());
	        row.createCell(cellCount++).setCellValue(vo.getzfacdo());
	        row.createCell(cellCount++).setCellValue(vo.getbelnr());
	        
	        row.createCell(cellCount++).setCellValue(vo.getbldat());
	        row.createCell(cellCount++).setCellValue(vo.getbudat());
	        row.createCell(cellCount++).setCellValue(vo.getlifnr());
	        row.createCell(cellCount++).setCellValue(vo.getName1());
	        
	        
	        row.createCell(cellCount++).setCellValue(vo.getzfcstgrp());
	        row.createCell(cellCount++).setCellValue(vo.getmwskz());
	        row.createCell(cellCount++).setCellValue(vo.getwmwst());
	        row.createCell(cellCount++).setCellValue(vo.getzfcd());
	        row.createCell(cellCount++).setCellValue(vo.getn16());
	        
	    }
	 
	    String fileName ="Sap전표.xls";
	    fileName  = URLEncoder.encode(fileName,"UTF-8");
	    
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("application/vnd.ms-excel;charset=EUC-KR");
	    response.setHeader("Pragma", "public");
	    response.setHeader("Expires", "0");
	    response.setHeader("Content-Disposition", "attachment; filename=" +fileName);
	
	    
	   OutputStream os = null;
	   //HSSFWorkbook workbook = null;
	   
	   try {
	       //workbook = (HSSFWorkbook) model.get("workbook");
	       os = response.getOutputStream();
	       
	       // 파일생성
	       workbook.write(os);
	   }catch (Exception e) {
	       e.printStackTrace();
	   } finally {
	       if(workbook != null) {
	           try {
	//               workbook.close();
	           } catch (Exception e) {
	               e.printStackTrace();
	           }
	       }
	       
	       if(os != null) {
	           try {
	               os.close();
	           } catch (Exception e) {
	               e.printStackTrace();
	           }
	       }
	   }
	}
	
	
	/**
     *SAP 다운을 위한  목록을 조회한다.
     */
    @RequestMapping("/cop/bbs/SelectBBSSapInfs.do")
    public String selectBBSSapInfs(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
    	
    	if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
    	
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    	Calendar cal = Calendar.getInstance();
    	
    	String firstDate = boardMasterVO.getSearchBgnDe();
    	String lastDate = boardMasterVO.getSearchEndDe();
    	 
    	if ((boardMasterVO.getSearchBgnDe() == null) || (boardMasterVO.getSearchBgnDe().equals(""))) {
        	cal.set(Calendar.DATE, 1);
       	 
            firstDate = formatter.format(cal.getTime());
            
        	boardMasterVO.setSearchBgnDe(firstDate);
    	}
    	if ((boardMasterVO.getSearchEndDe() == null) || (boardMasterVO.getSearchEndDe().equals(""))) {
        	cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        	
        	lastDate = formatter.format(cal.getTime());
    		boardMasterVO.setSearchEndDe(lastDate);
    	}
    	
    	boardMasterVO.setConfirmYn("1");
    	
    	Map<String, Object> map = bbsAttrbService.selectBBSMasterInfs(boardMasterVO);

		model.addAttribute("resultList", map.get("resultList"));

		return "cop/bbs/EgovBoardSapList";
    }
    
    /**
     *SAP 다운을 위한  목록을 조회한다.
     */
    @RequestMapping("cop/bbs/SelectBBSSapDetailInfs.do")
    public String SelectBBSSapDetailInfs(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
    	
    	if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    	Calendar cal = Calendar.getInstance();
    	
    	String firstDate = boardMasterVO.getSearchBgnDe();
    	String lastDate = boardMasterVO.getSearchEndDe();
    	
    	String searchDate = boardMasterVO.getSearchBgnDe();
    	
    	if ((boardMasterVO.getSearchGubun() == null) || (boardMasterVO.getSearchGubun().equals(""))) {
        	boardMasterVO.setSearchGubun("BL");
    	}
    	if ((boardMasterVO.getSearchBgnDe() == null) || (boardMasterVO.getSearchBgnDe().equals(""))) {
        	cal.set(Calendar.DATE, 1);
       	 
            firstDate = formatter.format(cal.getTime());
            
        	boardMasterVO.setSearchBgnDe(firstDate);
    	}
    	if ((boardMasterVO.getSearchEndDe() == null) || (boardMasterVO.getSearchEndDe().equals(""))) {
        	cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        	
        	lastDate = formatter.format(cal.getTime());
    		boardMasterVO.setSearchEndDe(lastDate);
    	}
    	
    	if ((boardMasterVO.getSearchBl() != null) && !(boardMasterVO.getSearchBl().equals(""))) {
        	
    		boardMasterVO.setSearchBlNo(boardMasterVO.getSearchBl());
    	}
    	 
    	if ((searchDate != null) && !(searchDate.equals(""))) {
        	boardMasterVO.setConfirmYn("1");
        	
        	Map<String, Object> map = bbsAttrbService.selectSapSubList(boardMasterVO);
        	
        	List<BoardMasterVO> data =  (List<BoardMasterVO>) map.get("resultList");
        	
        	//GROUP BY된 데이터를 받을 MAP
        	Map<String, Map<String, Object>> resultMap = new HashMap<String, Map<String, Object>>();

        	for(int i=0; i<data.size(); i++){
        		String orderNumber = data.get(i).getwaers().toString(); //KEY VALUE
        		if(resultMap.containsKey(orderNumber)){
        			//KEY값이 존재하면 해당 키값의 해당되는 가격을 가져와 더해줌
        			resultMap.get(orderNumber).put("wrbtr", Double.parseDouble(resultMap.get(orderNumber).get("wrbtr").toString()) 
        			+ Double.parseDouble(data.get(i).getwrbtr().toString()));
        			resultMap.get(orderNumber).put("dmbtr", Double.parseDouble(resultMap.get(orderNumber).get("dmbtr").toString()) 
        			+ Double.parseDouble(data.get(i).getdmbtr().toString()));
        			resultMap.get(orderNumber).put("wmwst", Double.parseDouble(resultMap.get(orderNumber).get("wmwst").toString()) 
        			+ Double.parseDouble(data.get(i).getwmwst().toString()));
        		}else{
        			//KEY값이 존재하지 않으면 MAP에 데이터를 넣어줌
        			Map<String, Object> dataMap = new HashMap<String, Object>();
        			dataMap.put("wrbtr", Double.parseDouble(data.get(i).getwrbtr().toString()));
        			dataMap.put("dmbtr", Double.parseDouble(data.get(i).getdmbtr().toString()));
        			dataMap.put("wmwst", Double.parseDouble(data.get(i).getwmwst().toString()));
        			resultMap.put(orderNumber, dataMap);
        		}
        	}

        	
        	
    		model.addAttribute("resultList", map.get("resultList"));
    		model.addAttribute("totalList", resultMap);
    	}
    	


		return "cop/bbs/EgovBoardSapDetailList";
    }
    
    /**
     *SAP 다운을 위한  목록을 조회한다.
     */
    @RequestMapping("/cop/bbs/SelectBBSSapSubInfs.do")
    public String selectBBSSapSubInfs(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
    	
    	if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
    	
    	//Map<String, Object> map = bbsAttrbService.selectSapSubBLList(boardMasterVO);
    	boardMasterVO.setProcessflag("LIST");
    	Map<String, Object> map = bbsAttrbService.selectSapSubBLSPList(boardMasterVO);    	
    	
		model.addAttribute("resultList", map.get("resultList"));
		model.addAttribute("searchBgnDe", boardMasterVO.getSearchBgnDe());
		model.addAttribute("searchEndDe", boardMasterVO.getSearchEndDe());
		model.addAttribute("searchBl", boardMasterVO.getSearchBl());
		model.addAttribute("t3_confirm",  map.get("t3_confirm"));
		model.addAttribute("t3_preconfirm",  map.get("t3_preconfirm"));

		return "cop/bbs/EgovBoardSapSubList";
    } 
    
    
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/cop/bbs/selectBBSSAPBktxtInfs.do", method = {RequestMethod.GET, RequestMethod.POST})
    public void selectBBSSAPBktxtInfs(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, HttpServletResponse response) throws Exception {
    	
    	List<BoardMasterVO> list = bbsAttrbService.selectBBSSAPBktxtInfs(boardMasterVO);
    	
    	// 응답해야 하는 문자열 : [{label:~,value:~},{label:~,value:~}]
        JSONArray array = new JSONArray();
        for(BoardMasterVO dto : list) {
            JSONObject obj = new JSONObject();
            obj.put("label", dto.getbktxt());
            obj.put("value", dto.getbktxt());
            array.add(obj);
        }
        PrintWriter out = response.getWriter();
        
        out.print(array.toString());
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/cop/bbs/selectBBSSAPLifnrInfs.do", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void selectBBSSAPLifnrInfs(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, HttpServletResponse response)
    		throws Exception {
    	
    	List<BoardMasterVO> list = bbsAttrbService.selectBBSSAPLifnrInfs(boardMasterVO);
    	
        
    	// 응답해야 하는 문자열 : [{label:~,value:~},{label:~,value:~}]
        JSONArray array = new JSONArray();
        for(BoardMasterVO dto : list) {
            JSONObject obj = new JSONObject();
        	// 한글깨짐 방지를 위해 인코딩하기

            obj.put("label", dto.getExtra_company());
            obj.put("value", dto.getExtra_company());
            array.add(obj);
        }
        
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        out.print(array.toString());
    }
    
    
    /**
     *RPA 목록을 조회한다.
     */
    @RequestMapping("/cop/bbs/selectExtraSPList.do")
    public String selectExtraSPList(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
    	
    	if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
    	
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM", Locale.KOREA);
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.MONTH , -1);

    	String firstDate = boardMasterVO.getSearchMonth();
    	 
    	if ((boardMasterVO.getSearchMonth() == null) || (boardMasterVO.getSearchMonth().equals(""))) {
    		cal.set(Calendar.DATE, 1);
    		 
    	    firstDate = formatter.format(cal.getTime());
    	    
    		boardMasterVO.setSearchMonth(firstDate);
    	}

    	Map<String, Object> map = bbsAttrbService.selectExtraSPList(boardMasterVO);
    	
    	
    	model.addAttribute("extraList", map.get("extraList"));
    	model.addAttribute("extra1List", map.get("extra1List"));
    	model.addAttribute("resultTotal", map.get("resultTotal"));

		return "cop/bbs/EgovBoardExtraList";
    }
    
    /**
     *RPA 목록을 조회한다.
     */
    @RequestMapping("/cop/bbs/selectYetArrivedList.do")
    public String selectYetArrivedList(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
    	
    	if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
    	
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM", Locale.KOREA);
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.MONTH , -1);

    	String firstDate = boardMasterVO.getSearchMonth();
    	 
    	if ((boardMasterVO.getSearchMonth() == null) || (boardMasterVO.getSearchMonth().equals(""))) {
    		cal.set(Calendar.DATE, 1);
    		 
    	    firstDate = formatter.format(cal.getTime());
    	    
    		boardMasterVO.setSearchMonth(firstDate);
    	}

    	Map<String, Object> map = bbsAttrbService.selectYetArrivedList(boardMasterVO);
    	
    	model.addAttribute("yetList", map.get("yetList"));
    	model.addAttribute("yetTotal", map.get("yetTotal"));
    	model.addAttribute("resultMap", map.get("resultMap"));
    	model.addAttribute("resultMap2", map.get("resultMap2"));

		return "cop/bbs/EgovBoardYetArrivedList";
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody    
    @RequestMapping("/cop/bbs/selectYetArrivedExcel.do")
    public Map<Object, Object> selectYetArrivedExcel(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	Map<Object, Object> returnMap = new HashMap<Object, Object>();
    	
		try {
	    	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM", Locale.KOREA);
	    	SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
	    	Calendar cal = Calendar.getInstance();

	    	if ((boardMasterVO.getSearchMonth() == null) || (boardMasterVO.getSearchMonth().equals(""))) {
	    		cal.set(Calendar.DATE, 1);
	    		 
	    		String firstDate = formatter.format(cal.getTime());
	    	    
	    		boardMasterVO.setSearchMonth(firstDate);
	    	}
	    	
	    	String searchMon = boardMasterVO.getSearchMonth();
	    	
	    	Map<String, Object> map = bbsAttrbService.selectYetArrivedList(boardMasterVO);
			
        	List<BoardMasterVO> lstResult =  (List<BoardMasterVO>) map.get("yetTotal");
        	
        	//GROUP BY된 데이터를 받을 MAP
        	Map<String, Map<String, Object>> resultMap = new HashMap<String, Map<String, Object>>();

        	for(int i=0; i<lstResult.size(); i++){
        		String orderNumber = "total";
        		if(resultMap.containsKey(orderNumber)){
        			resultMap.get(orderNumber).put("blamt", Double.parseDouble(resultMap.get(orderNumber).get("blamt").toString()) 
        			+ Double.parseDouble(lstResult.get(i).getBlamt().toString()));
        		}else{
        			//KEY값이 존재하지 않으면 MAP에 데이터를 넣어줌
        			Map<String, Object> dataMap = new HashMap<String, Object>();
        			dataMap.put("blamt", Double.parseDouble(lstResult.get(i).getBlamt().toString()));
        			resultMap.put(orderNumber, dataMap);
        		}
        	}
        	
        	
        	Set set = resultMap.entrySet();
        	Iterator iterator = set.iterator();

        	while(iterator.hasNext()){
        	  Map.Entry entry = (Map.Entry)iterator.next();
        	  String key = (String)entry.getKey();
        	  Map<String, Object> valueMap = (Map<String, Object>) entry.getValue();
        	  
       	  
        	  BoardMasterVO vo = new BoardMasterVO();
        	  vo.setLastday("합계");

              vo.setBlamt(valueMap.get("blamt").toString());
              lstResult.add(vo);

        	}
        	
			String excelTitle = " 미  착  품  명  세  서 ";

        	
			Map<String, Object> tempMap = new HashMap<String, Object>();
    	
			//첫Row,마지막Row,첫cell,마지막cell, row높이, 스타일, 내용
			Map<String, Object> headerMap = new HashMap<String, Object>();
			
			headerMap.put("sRow", "1");
			headerMap.put("eRow", "1");
			headerMap.put("sCol", "0");
			headerMap.put("eCol", "4");
			headerMap.put("fontType", "titleLine");
			headerMap.put("fontColor", "000000");
			headerMap.put("styleColor", "FFFFFF");
			headerMap.put("textAlign", "center");
			headerMap.put("textVAlign", "center");
			headerMap.put("line", "none");
			headerMap.put("title", excelTitle);
			
			//첫Row,마지막Row,첫cell,마지막cell, row높이, 스타일, 내용
			Map<String, Object> unitMap = new HashMap<String, Object>();
			
			unitMap.put("sRow", "2");
			unitMap.put("eRow", "2");
			unitMap.put("sCol", "0");
			unitMap.put("eCol", "4");
			unitMap.put("fontType", "unitLine");
			unitMap.put("fontColor", "000000");
			unitMap.put("styleColor", "FFFFFF");
			unitMap.put("textAlign", "left");
			unitMap.put("textVAlign", "center");
			unitMap.put("line", "none");
			unitMap.put("title", "");
			
			List<Map<String, Object>> unitList = new ArrayList<Map<String, Object>>();
			
			tempMap = new HashMap<String, Object>();
			tempMap.put("sRow", "2");
			tempMap.put("eRow", "2");
			tempMap.put("sCol", "0");
			tempMap.put("eCol", "1");
			tempMap.put("fontType", "unitLine");
			tempMap.put("fontColor", "000000");
			tempMap.put("styleColor", "FFFFFF");
			tempMap.put("textAlign", "left");
			tempMap.put("textVAlign", "center");
			tempMap.put("line", "none");
			tempMap.put("title", "업체명 : 우진공업㈜");
			unitList.add(tempMap);
			
			tempMap = new HashMap<String, Object>();
			tempMap.put("sRow", "2");
			tempMap.put("eRow", "2");
			tempMap.put("sCol", "4");
			tempMap.put("eCol", "4");
			tempMap.put("fontType", "unitLine");
			tempMap.put("fontColor", "000000");
			tempMap.put("styleColor", "FFFFFF");
			tempMap.put("textAlign", "right");
			tempMap.put("textVAlign", "center");
			tempMap.put("line", "none");
			tempMap.put("title", "(단위 : 원)");
			unitList.add(tempMap);
			
			
			Map<String, Object> titleStyleMap = new HashMap<String, Object>();
			
			titleStyleMap.put("sRow", "3");
			titleStyleMap.put("eRow", "3");
			titleStyleMap.put("sCol", "0");
			titleStyleMap.put("eCol", "4");
			titleStyleMap.put("fontType", "subtitle");
			titleStyleMap.put("fontColor", "000000");
			titleStyleMap.put("styleColor", "ECF5FC");
			titleStyleMap.put("textAlign", "center");
			titleStyleMap.put("textVAlign", "center");
			titleStyleMap.put("line", "line");
			
			List<Map<String, Object>> fieldInfoList = new ArrayList<Map<String, Object>>();
			
			tempMap = new HashMap<String, Object>();
			tempMap.put("field", "lastday");
			tempMap.put("cellTitle", "일   자");
			tempMap.put("fileType", "String");
			tempMap.put("cellWidth", 13*256);
			tempMap.put("fontType", "content");
			tempMap.put("fontColor", "000000");
			tempMap.put("styleColor", "FFFFFF");
			tempMap.put("textAlign", "center");
			tempMap.put("textVAlign", "center");
			tempMap.put("line", "dot");
			tempMap.put("fomule", "");
			fieldInfoList.add(tempMap);
			
			tempMap = new HashMap<String, Object>();
			tempMap.put("field", "bktxt");
			tempMap.put("cellTitle", "관리KEY명");
			tempMap.put("fileType", "String");
			tempMap.put("cellWidth", 23*256);
			tempMap.put("fontType", "content");
			tempMap.put("fontColor", "000000");
			tempMap.put("styleColor", "FFFFFF");
			tempMap.put("textAlign", "center");
			tempMap.put("textVAlign", "center");
			tempMap.put("line", "dot");
			tempMap.put("fomule", "");
			fieldInfoList.add(tempMap);
			
			tempMap = new HashMap<String, Object>();
			tempMap.put("field", "item");
			tempMap.put("cellTitle", "적         요");
			tempMap.put("fileType", "String");
			tempMap.put("cellWidth", 25*256);
			tempMap.put("fontType", "content");
			tempMap.put("fontColor", "000000");
			tempMap.put("styleColor", "FFFFFF");
			tempMap.put("textAlign", "center");
			tempMap.put("textVAlign", "center");
			tempMap.put("line", "dot");
			tempMap.put("fomule", "");
			fieldInfoList.add(tempMap);
			
			tempMap = new HashMap<String, Object>();
			tempMap.put("field", "blamt");
			tempMap.put("cellTitle", "금       액");
			tempMap.put("fileType", "Int");
			tempMap.put("cellWidth", 15*256);
			tempMap.put("fontType", "content");
			tempMap.put("fontColor", "000000");
			tempMap.put("styleColor", "FFFFFF");
			tempMap.put("textAlign", "right");
			tempMap.put("textVAlign", "center");
			tempMap.put("line", "dot");
			tempMap.put("fomule", "* #,##0_-;-* #,##0_-;_-* \"-\"_-;_-@_-");
			fieldInfoList.add(tempMap);
			
			tempMap = new HashMap<String, Object>();
			tempMap.put("field", "");
			tempMap.put("cellTitle", "비   고");
			tempMap.put("fileType", "String");
			tempMap.put("cellWidth", 13*256);
			tempMap.put("fontType", "content");
			tempMap.put("fontColor", "000000");
			tempMap.put("styleColor", "FFFFFF");
			tempMap.put("textAlign", "center");
			tempMap.put("textVAlign", "center");
			tempMap.put("line", "dot");
			tempMap.put("fomule", "");
			fieldInfoList.add(tempMap);
					
			Map<String, Object> excelInfpMap = new HashMap<String, Object>();
			excelInfpMap.put("headerMap", headerMap);		
			excelInfpMap.put("unitMap", unitMap);
			excelInfpMap.put("titleStyleMap", titleStyleMap);
			excelInfpMap.put("unitList", unitList);
			excelInfpMap.put("fieldInfoList", fieldInfoList);
			
			
			SXSSFWorkbook workbook = bbsAttrbService.buildExcelXSS(excelTitle,excelInfpMap, lstResult, false);

			String realExcelFilename = null;
			realExcelFilename = URLEncoder.encode(excelTitle, "UTF-8");
			realExcelFilename = realExcelFilename.replaceAll("\\+", " ");

			/*
			 * HTTP Header 설정.
			 */
			response.setContentType("application/vnd.ms-excel; name=\"" + realExcelFilename + ".xlsx\"");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + realExcelFilename + ".xlsx\"");
			response.setHeader("Content-Transfer-Encoding", "binary");
//            response.setHeader("Content-Length", Long.toString(fileDownLoadInputVO.getlFileSize()));
			response.setHeader("Cache-Control", "no-cahe, no-store, must-revalidate\r\n");
			response.setHeader("Connection", "close");

			//FileOutputStream fileOut = new FileOutputStream(realExcelFilename + ".xlsx");
			//OutputStream out = response.getOutputStream();
			OutputStream fileOut = response.getOutputStream();

			workbook.write(fileOut);
			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}


		return returnMap;
    }
    
    
    
    /**
     *RPA 목록을 조회한다.
     */
    @RequestMapping("/cop/bbs/selectExtraSPFinishList.do")
    public String selectExtraSPFinishList(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
    	
    	if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    	Calendar cal = Calendar.getInstance();
    	
    	String firstDate = boardMasterVO.getSearchBgnDe();
    	String lastDate = boardMasterVO.getSearchEndDe();
    	
    	if ((boardMasterVO.getSearchGubun() == null) || (boardMasterVO.getSearchGubun().equals(""))) {
        	boardMasterVO.setSearchGubun("BL");
    	}
    	if ((boardMasterVO.getSearchBgnDe() == null) || (boardMasterVO.getSearchBgnDe().equals(""))) {
        	cal.set(Calendar.DATE, 1);
       	 
            firstDate = formatter.format(cal.getTime());
            
        	boardMasterVO.setSearchBgnDe(firstDate);
    	}
    	if ((boardMasterVO.getSearchEndDe() == null) || (boardMasterVO.getSearchEndDe().equals(""))) {
        	cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        	
        	lastDate = formatter.format(cal.getTime());
    		boardMasterVO.setSearchEndDe(lastDate);
    	}
    	
    	List<BoardMasterVO> list = bbsAttrbService.selectExtraSPFinishList(boardMasterVO);
    	
    	
    	model.addAttribute("extraList", list);

		return "cop/bbs/EgovBoardExtraList1";
    }
    
    /**
     *RPA 목록을 조회한다.
     */
    @RequestMapping("/cop/bbs/selectTax.do")
    public String selectTax(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
    	
    	if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
    	SimpleDateFormat formatterMon = new SimpleDateFormat("MM", Locale.KOREA);

    	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    	Calendar cal = Calendar.getInstance();
    	
    	String firstMon = boardMasterVO.getSearchMonth();
    	String firstDate = boardMasterVO.getSearchBgnDe();
    	String lastDate = boardMasterVO.getSearchEndDe();
    	if ((boardMasterVO.getSearchMonth() == null) || (boardMasterVO.getSearchMonth().equals(""))) {
        	cal.set(Calendar.DATE, 1);
        	
        	firstMon = formatterMon.format(cal.getTime());
    	    
    		boardMasterVO.setSearchMonth(String.valueOf(Integer.parseInt(firstMon)));
       	 
    	}
    	
    	if ((boardMasterVO.getSearchBgnDe() == null) || (boardMasterVO.getSearchBgnDe().equals(""))) {
        	cal.set(Calendar.DATE, 1);
        	
        	firstMon = formatterMon.format(cal.getTime());
    	    
    		//boardMasterVO.setSearchMonth(String.valueOf(Integer.parseInt(firstMon)));
       	 
            firstDate = formatter.format(cal.getTime());
            
        	boardMasterVO.setSearchBgnDe(firstDate);
    	}
    	else {
    		Date date = new SimpleDateFormat("yyyyMMdd").parse(firstDate);
    		String newstring = new SimpleDateFormat("yyyyMMdd").format(date);
    		cal.set(Calendar.YEAR, Integer.parseInt(newstring.substring(0, 4)));
    		cal.set(Calendar.MONTH, Integer.parseInt(newstring.substring(4, 6)) - 1);
    		cal.set(Calendar.DATE, Integer.parseInt(newstring.substring(6, 8)));


//    		cal.set(tempDate.getYear(), tempDate.getMonth(), tempDate.getDay());
    		int ny = cal.get(Calendar.YEAR);
    		int nm = cal.get(Calendar.MONTH) + 1;
    		int nd = cal.get(Calendar.DATE);
    		//boardMasterVO.setSearchMonth(String.valueOf(nm));
    	
    	}
    	if ((boardMasterVO.getSearchEndDe() == null) || (boardMasterVO.getSearchEndDe().equals(""))) {
        	cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        	
        	lastDate = formatter.format(cal.getTime());
    		boardMasterVO.setSearchEndDe(lastDate);
    	}
    	String firstExtraDate = boardMasterVO.getSearchExtraBgnDe();
    	String lastExtraDate = boardMasterVO.getSearchExtraEndDe();

    	if ((boardMasterVO.getSearchExtraBgnDe() == null) || (boardMasterVO.getSearchExtraBgnDe().equals(""))) {
        	cal.set(Calendar.DATE, 1);
       	 
            firstExtraDate = formatter.format(cal.getTime());
            
        	boardMasterVO.setSearchExtraBgnDe(firstExtraDate);
    	}
    	if ((boardMasterVO.getSearchExtraEndDe() == null) || (boardMasterVO.getSearchExtraEndDe().equals(""))) {
        	cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        	
        	lastExtraDate = formatter.format(cal.getTime());
    		boardMasterVO.setSearchExtraEndDe(lastExtraDate);
    	}
    	
    	Map<String, Object> map = bbsAttrbService.selectExtraTaxSum(boardMasterVO);
    	
    	
    	model.addAttribute("resultList", map.get("resultList"));

		return "cop/bbs/EgovBoardTax";
    }
    
    @SuppressWarnings({ "unchecked", "deprecation" })
	@ResponseBody    
    @RequestMapping("/cop/bbs/selectExtraTaxExcel.do")
    public Map<Object, Object> selectExtraTaxExcel(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	Map<Object, Object> returnMap = new HashMap<Object, Object>();
    	
		try {
	    	String getSearchMonth = boardMasterVO.getSearchMonth();
	    	String firstDate = boardMasterVO.getSearchBgnDe();
	    	String lastDate = boardMasterVO.getSearchEndDe();
	    	String firstExtraDate = boardMasterVO.getSearchExtraBgnDe();
	    	String lastExtraDate = boardMasterVO.getSearchExtraEndDe();
			String excelTitle = getSearchMonth + "월 관부가세 마감 내역";
			
			Map<String, Object> map = bbsAttrbService.selectExtraTaxSum(boardMasterVO);
	    	
	    	
			List<BoardMasterVO> lstResult = (List<BoardMasterVO>) map.get("resultList");
    	
			//첫Row,마지막Row,첫cell,마지막cell, row높이, 스타일, 내용
			Map<String, Object> headerMap = new HashMap<String, Object>();
			
			headerMap.put("sRow", "1");
			headerMap.put("eRow", "1");
			headerMap.put("sCol", "0");
			headerMap.put("eCol", "5");
			headerMap.put("fontType", "titleLine");
			headerMap.put("fontColor", "000000");
			headerMap.put("styleColor", "FFFFFF");
			headerMap.put("textAlign", "center");
			headerMap.put("textVAlign", "center");
			headerMap.put("line", "none");
			headerMap.put("title", excelTitle);
			
			Map<String, Object> titleStyleMap = new HashMap<String, Object>();
			
			titleStyleMap.put("sRow", "3");
			titleStyleMap.put("eRow", "4");
			titleStyleMap.put("sCol", "0");
			titleStyleMap.put("eCol", "5");
			titleStyleMap.put("fontType", "subtitle");
			titleStyleMap.put("fontColor", "000000");
			titleStyleMap.put("styleColor", "ECF5FC");
			titleStyleMap.put("textAlign", "center");
			titleStyleMap.put("textVAlign", "center");
			titleStyleMap.put("line", "line");
			
			List<Map<String, Object>> titleCellList = new ArrayList<Map<String, Object>>();
			Map<String, Object> titleCellMap = new HashMap<String, Object>();
			
			titleCellMap.put("sRow", "3");
			titleCellMap.put("eRow", "4");
			titleCellMap.put("sCol", "0");
			titleCellMap.put("eCol", "0");
			titleCellMap.put("cellWidth", 35*256);
			titleCellMap.put("cellTitle", "관세사");
			titleCellList.add(titleCellMap);
			
			titleCellMap = new HashMap<String, Object>();
			titleCellMap.put("sRow", "3");
			titleCellMap.put("eRow", "4");
			titleCellMap.put("sCol", "1");
			titleCellMap.put("eCol", "1");
			titleCellMap.put("cellWidth", 25*256);
			titleCellMap.put("cellTitle", "세관");
			titleCellList.add(titleCellMap);
			
			titleCellMap = new HashMap<String, Object>();
			titleCellMap.put("sRow", "3");
			titleCellMap.put("eRow", "3");
			titleCellMap.put("sCol", "2");
			titleCellMap.put("eCol", "4");
			titleCellMap.put("cellWidth", 13*256);
			titleCellMap.put("cellTitle", "금액(\\)");
			titleCellList.add(titleCellMap);
			
			titleCellMap = new HashMap<String, Object>();
			titleCellMap.put("sRow", "4");
			titleCellMap.put("eRow", "4");
			titleCellMap.put("sCol", "2");
			titleCellMap.put("eCol", "2");
			titleCellMap.put("cellWidth", 13*256);
			titleCellMap.put("cellTitle", "관세");
			titleCellList.add(titleCellMap);
			
			titleCellMap = new HashMap<String, Object>();
			titleCellMap.put("sRow", "4");
			titleCellMap.put("eRow", "4");
			titleCellMap.put("sCol", "3");
			titleCellMap.put("eCol", "3");
			titleCellMap.put("cellWidth", 13*256);
			titleCellMap.put("cellTitle", "부가세");
			titleCellList.add(titleCellMap);
			
			titleCellMap = new HashMap<String, Object>();
			titleCellMap.put("sRow", "4");
			titleCellMap.put("eRow", "4");
			titleCellMap.put("sCol", "4");
			titleCellMap.put("eCol", "4");
			titleCellMap.put("cellWidth", 13*256);
			titleCellMap.put("cellTitle", "Total");
			titleCellList.add(titleCellMap);
			
			titleCellMap = new HashMap<String, Object>();
			titleCellMap.put("sRow", "3");
			titleCellMap.put("eRow", "4");
			titleCellMap.put("sCol", "5");
			titleCellMap.put("eCol", "5");
			titleCellMap.put("cellWidth", 13*256);
			titleCellMap.put("cellTitle", "지급일자");
			titleCellList.add(titleCellMap);
			
			List<Map<String, Object>> fieldInfoList = new ArrayList<Map<String, Object>>();
			Map<String, Object> fieldInfoMap = new HashMap<String, Object>();
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "zfapnm");
			fieldInfoMap.put("cellTitle", "관세사");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 55*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			
			fieldInfoList.add(fieldInfoMap);
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "name1");
			fieldInfoMap.put("cellTitle", "세관");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 35*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "dmbtr");
			fieldInfoMap.put("cellTitle", "관세");
			fieldInfoMap.put("fileType", "Int");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "right");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "* #,##0_-;-* #,##0_-;_-* \"-\"_-;_-@_-");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "wrbtr");
			fieldInfoMap.put("cellTitle", "부가세");
			fieldInfoMap.put("fileType", "Int");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "right");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "* #,##0_-;-* #,##0_-;_-* \"-\"_-;_-@_-");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "twrbtr");
			fieldInfoMap.put("cellTitle", "Total");
			fieldInfoMap.put("fileType", "Int");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "right");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "* #,##0_-;-* #,##0_-;_-* \"-\"_-;_-@_-");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "bldat");
			fieldInfoMap.put("cellTitle", "지급일자");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			Map<String, Object> mergeCellMap = new HashMap<String, Object>();
			
			mergeCellMap.put("zfapnm", "1");
			mergeCellMap.put("name1", "2");
					
			Map<String, Object> excelInfpMap = new HashMap<String, Object>();
			excelInfpMap.put("headerMap", headerMap);		
			excelInfpMap.put("titleStyleMap", titleStyleMap);
			excelInfpMap.put("mergeCellMap", mergeCellMap);
			excelInfpMap.put("titleCellList", titleCellList);
			excelInfpMap.put("fieldInfoList", fieldInfoList);
			
			
			SXSSFWorkbook workbook = bbsAttrbService.buildExcelXSS(excelTitle,excelInfpMap, lstResult, false);

			String realExcelFilename = null;
			realExcelFilename = URLEncoder.encode(excelTitle, "UTF-8");
			realExcelFilename = realExcelFilename.replaceAll("\\+", " ");

			/*
			 * HTTP Header 설정.
			 */
			response.setContentType("application/vnd.ms-excel; name=\"" + realExcelFilename + ".xlsx\"");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + realExcelFilename + ".xlsx\"");
			response.setHeader("Content-Transfer-Encoding", "binary");
//            response.setHeader("Content-Length", Long.toString(fileDownLoadInputVO.getlFileSize()));
			response.setHeader("Cache-Control", "no-cahe, no-store, must-revalidate\r\n");
			response.setHeader("Connection", "close");

			//FileOutputStream fileOut = new FileOutputStream(realExcelFilename + ".xlsx");
			//OutputStream out = response.getOutputStream();
			OutputStream fileOut = response.getOutputStream();

			workbook.write(fileOut);
			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}


		return returnMap;
    }
    
    /**
     *RPA 목록을 조회한다.
     */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@ResponseBody
    @RequestMapping("/cop/bbs/selectExtraTax.do")
    public Map<Object, Object> selectExtraTax(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	Map<Object, Object> returnMap = new HashMap<Object, Object>();
    	
		try {
			
	    	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM", Locale.KOREA);
	    	SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
	    	Calendar cal = Calendar.getInstance();
	    	//cal.add(Calendar.MONTH , -1);

	    	String checkMonth = boardMasterVO.getSearchMonth();
	    	
	    	String firstDate = boardMasterVO.getSearchBgnDe();
	    	String lastDate = boardMasterVO.getSearchEndDe();
	    	
	    	String searchDate = boardMasterVO.getSearchBgnDe();
	    	
	    	if ((boardMasterVO.getSearchBgnDe() == null) || (boardMasterVO.getSearchBgnDe().equals(""))) {
	        	cal.set(Calendar.DATE, 1);
	       	 
	            firstDate = formatter.format(cal.getTime());
	            
	        	boardMasterVO.setSearchBgnDe(firstDate);
	    	}
	    	if ((boardMasterVO.getSearchEndDe() == null) || (boardMasterVO.getSearchEndDe().equals(""))) {
	        	cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
	        	
	        	lastDate = formatter.format(cal.getTime());
	    		boardMasterVO.setSearchEndDe(lastDate);
	    	}
	    	 
	    	/*if ((boardMasterVO.getSearchMonth() == null) || (boardMasterVO.getSearchMonth().equals(""))) {
	    		cal.set(Calendar.DATE, 1);
	    		 
	    		checkMonth = formatter.format(cal.getTime());
	    	    
	    		boardMasterVO.setSearchMonth(checkMonth);
	    		
	        	cal.set(Calendar.DATE, 1);
	            firstDate = formatter1.format(cal.getTime());
	        	boardMasterVO.setSearchBgnDe(firstDate);
	        	
	        	cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
	        	lastDate = formatter1.format(cal.getTime());
	    		boardMasterVO.setSearchEndDe(lastDate);
	    	}
	    	else {
	    		Date tempDate = formatter1.parse(checkMonth+"01");
	    		Date date = new SimpleDateFormat("yyyyMMdd").parse(checkMonth+"01");
	    		String newstring = new SimpleDateFormat("yyyyMMdd").format(date);
	    		cal.set(Calendar.YEAR, Integer.parseInt(newstring.substring(0, 4)));
	    		cal.set(Calendar.MONTH, Integer.parseInt(newstring.substring(4, 6)) - 1);
	    		cal.set(Calendar.DATE, Integer.parseInt(newstring.substring(6, 8)));


//	    		cal.set(tempDate.getYear(), tempDate.getMonth(), tempDate.getDay());
	    		int ny = cal.get(Calendar.YEAR);
	    		int nm = cal.get(Calendar.MONTH) + 1;
	    		int nd = cal.get(Calendar.DATE);
	    		
	    		cal.set(Calendar.DATE, 1);
	            firstDate = formatter1.format(cal.getTime());
	        	boardMasterVO.setSearchBgnDe(firstDate);
	        	
	        	cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
	        	lastDate = formatter1.format(cal.getTime());
	    		boardMasterVO.setSearchEndDe(lastDate);
	    	}
	    	*/
	    	
	    	bbsAttrbService.insertExtraTax(boardMasterVO);
	    	
	    	List<BoardMasterVO> lstResult = bbsAttrbService.selectExtraTax(boardMasterVO);
	    	
	    	String excelTitle = boardMasterVO.getSearchMonth() + "_부대비용 부가세 정보";
			
			Map<String, Object> titleStyleMap = new HashMap<String, Object>();
			
			titleStyleMap.put("sRow", "0");
			titleStyleMap.put("eRow", "0");
			titleStyleMap.put("sCol", "0");
			titleStyleMap.put("eCol", "16");
			titleStyleMap.put("fontType", "subtitle");
			titleStyleMap.put("fontColor", "000000");
			titleStyleMap.put("styleColor", "ECF5FC");
			titleStyleMap.put("textAlign", "center");
			titleStyleMap.put("textVAlign", "center");
			titleStyleMap.put("line", "line");
			
			
			List<Map<String, Object>> fieldInfoList = new ArrayList<Map<String, Object>>();
			Map<String, Object> fieldInfoMap = new HashMap<String, Object>();
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "");
			fieldInfoMap.put("cellTitle", "통관요청/입고요청 관리번호");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 15*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "");
			fieldInfoMap.put("cellTitle", "수입신고번호");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 10*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "");
			fieldInfoMap.put("cellTitle", "신고지 세");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 10*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "name1");
			fieldInfoMap.put("cellTitle", "이름 1");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 10*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "lifnr");
			fieldInfoMap.put("cellTitle", "공급업체");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "");
			fieldInfoMap.put("cellTitle", "사업자등록번호");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 10*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "extra_detail");
			fieldInfoMap.put("cellTitle", "헤더");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 15*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "");
			fieldInfoMap.put("cellTitle", "신고자상호");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 10*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "");
			fieldInfoMap.put("cellTitle", "총관세");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 10*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "right");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "extra_tax");
			fieldInfoMap.put("cellTitle", "부가세");
			fieldInfoMap.put("fileType", "Int");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "right");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "* #,##0_-;-* #,##0_-;_-* \"-\"_-;_-@_-");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "waers");
			fieldInfoMap.put("cellTitle", "원화통화");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "extra_price");
			fieldInfoMap.put("cellTitle", "공급가액");
			fieldInfoMap.put("fileType", "Int");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "right");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "* #,##0_-;-* #,##0_-;_-* \"-\"_-;_-@_-");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "waers");
			fieldInfoMap.put("cellTitle", "원화통화");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "extra_bldate");
			fieldInfoMap.put("cellTitle", "전기일");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "");
			fieldInfoMap.put("cellTitle", "자재구분");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 10*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "right");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "income_key");
			fieldInfoMap.put("cellTitle", "선기명");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "extra_bldate");
			fieldInfoMap.put("cellTitle", "전기일");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
					
			Map<String, Object> excelInfpMap = new HashMap<String, Object>();
			excelInfpMap.put("titleStyleMap", titleStyleMap);
			excelInfpMap.put("fieldInfoList", fieldInfoList);
			
			
			SXSSFWorkbook workbook = bbsAttrbService.buildExcelDownXSS(excelTitle,excelInfpMap, lstResult);

			String realExcelFilename = null;
			realExcelFilename = URLEncoder.encode(excelTitle, "UTF-8");
			realExcelFilename = realExcelFilename.replaceAll("\\+", " ");

			/*
			 * HTTP Header 설정.
			 */
			response.setContentType("application/vnd.ms-excel; name=\"" + realExcelFilename + ".xlsx\"");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + realExcelFilename + ".xlsx\"");
			response.setHeader("Content-Transfer-Encoding", "binary");
//            response.setHeader("Content-Length", Long.toString(fileDownLoadInputVO.getlFileSize()));
			response.setHeader("Cache-Control", "no-cahe, no-store, must-revalidate\r\n");
			response.setHeader("Connection", "close");

			//FileOutputStream fileOut = new FileOutputStream(realExcelFilename + ".xlsx");
			//OutputStream out = response.getOutputStream();
			OutputStream fileOut = response.getOutputStream();

			workbook.write(fileOut);
			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}


		return returnMap;
    }
    
    
    
    
    /**
     *RPA 목록을 조회한다.
     */
    @RequestMapping("/cop/bbs/selectExtraSubList.do")
    public String selectExtraSubList(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
    	
    	if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
    	try {
	    	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
	    	Calendar cal = Calendar.getInstance();
	    	
	    	String firstDate = boardMasterVO.getSearchBgnDe();
	    	String lastDate = boardMasterVO.getSearchEndDe();
	    	
	    	String searchDate = boardMasterVO.getSearchBgnDe();
	    	
	    	if ((boardMasterVO.getSearchBgnDe() == null) || (boardMasterVO.getSearchBgnDe().equals(""))) {
	        	cal.set(Calendar.DATE, 1);
	       	 
	            firstDate = formatter.format(cal.getTime());
	            
	        	boardMasterVO.setSearchBgnDe(firstDate);
	    	}
	    	if ((boardMasterVO.getSearchEndDe() == null) || (boardMasterVO.getSearchEndDe().equals(""))) {
	        	cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
	        	
	        	lastDate = formatter.format(cal.getTime());
	    		boardMasterVO.setSearchEndDe(lastDate);
	    	}
	    	
	    	if ((searchDate != null) && !(searchDate.equals(""))) {
	        	boardMasterVO.setConfirmYn("1");
	        	
	        	Map<String, Object> map = bbsAttrbService.selectStatement(boardMasterVO);
	        	
	        	List<BoardMasterVO> data =  (List<BoardMasterVO>) map.get("resultList");
	        	
	        	//GROUP BY된 데이터를 받을 MAP
	        	Map<String, Map<String, Object>> resultMap = new HashMap<String, Map<String, Object>>();
	
	        	for(int i=0; i<data.size(); i++){
	        		String orderNumber = data.get(i).getWaers1().toString(); //KEY VALUE
	        		if(resultMap.containsKey(orderNumber)){
	        			//KEY값이 존재하면 해당 키값의 해당되는 가격을 가져와 더해줌
	        			resultMap.get(orderNumber).put("twrbtr", Long.parseLong(resultMap.get(orderNumber).get("twrbtr").toString()) 
	        			+ data.get(i).getTwrbtr());
	        			resultMap.get(orderNumber).put("wrbtr_tax", Long.parseLong(resultMap.get(orderNumber).get("wrbtr_tax").toString()) 
	        			+ data.get(i).getWrbtr_tax());
	        			resultMap.get(orderNumber).put("extra_total", Long.parseLong(resultMap.get(orderNumber).get("extra_total").toString()) 
	        			+ Long.parseLong(data.get(i).getExtra_total()));
	        			resultMap.get(orderNumber).put("swrbtr", Long.parseLong(resultMap.get(orderNumber).get("swrbtr").toString()) 
	        			+ data.get(i).getSwrbtr());
	        		}else{
	        			//KEY값이 존재하지 않으면 MAP에 데이터를 넣어줌
	        			Map<String, Object> dataMap = new HashMap<String, Object>();
	        			dataMap.put("twrbtr", data.get(i).getTwrbtr());
	        			dataMap.put("wrbtr_tax", data.get(i).getWrbtr_tax());
	        			dataMap.put("extra_total", Long.parseLong(data.get(i).getExtra_total()));
	        			dataMap.put("swrbtr", data.get(i).getSwrbtr());
	        			resultMap.put(orderNumber, dataMap);
	        		}
	        	}
	
	        	
	        	
	    		model.addAttribute("resultList", map.get("resultList"));
	    		model.addAttribute("totalList", resultMap);
	    	}
	    } catch (Exception e) {
			e.printStackTrace();
		}

		return "cop/bbs/EgovBoardExtraSub";
    }
    
    
    /**
     *RPA 목록을 수정한다 kkk.
     */
    @RequestMapping(value="/cop/bbs/confirmStatement.do", method = {RequestMethod.GET, RequestMethod.POST})
    //public String updateBBSMasterInf(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO,  ModelMap model) throws Exception {
    public String confirmStatement(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
	
        bbsAttrbService.confirmStatement(boardMasterVO);
        
        return "forward:/cop/bbs/selectExtraSubList.do";
    }
    
    /**
     *RPA 목록을 조회한다.
     */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@ResponseBody
    @RequestMapping("/cop/bbs/selectSapListExcel.do")
    public Map<Object, Object> selectSapListExcel(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	Map<Object, Object> returnMap = new HashMap<Object, Object>();
    	
		try {
			SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd", Locale.KOREA );
			SimpleDateFormat formatter1 = new SimpleDateFormat( "yyyyMMdd", Locale.KOREA );
			Calendar cal = Calendar.getInstance();
	    	
	    	String searchBgnDe = boardMasterVO.getSearchBgnDe();
	    	String searchEndDe = boardMasterVO.getSearchEndDe();
	    	String searchBlNo = boardMasterVO.getSearchBlNo();
	    	String searchBgnDe1 = boardMasterVO.getSearchBgnDe();
	    	String searchEndDe1 = boardMasterVO.getSearchEndDe();
	    	 
	    	if ((boardMasterVO.getSearchBgnDe() == null) || (boardMasterVO.getSearchBgnDe().equals(""))) {
	        	cal.set(Calendar.DATE, 1);
	       	 
	        	searchBgnDe = formatter.format(cal.getTime());
	        	searchBgnDe1 = formatter1.format(cal.getTime());
	    	}
	    	else {
	    		Date tempDate = formatter1.parse(searchBgnDe);
	    		searchBgnDe = formatter.format(tempDate);
	    		
	    	}
	    	if ((boardMasterVO.getSearchEndDe() == null) || (boardMasterVO.getSearchEndDe().equals(""))) {
	        	cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
	        	
	        	searchEndDe = formatter.format(cal.getTime());
	        	searchEndDe1 = formatter1.format(cal.getTime());
	    	}
	    	else {
	    		Date tempDate = formatter1.parse(searchEndDe);
	    		searchEndDe = formatter.format(tempDate);
	    	}
	    	
	    	
	    	if ((boardMasterVO.getSearchBl() != null) && !(boardMasterVO.getSearchBl().equals(""))) {
	        	
	    		boardMasterVO.setSearchBlNo(boardMasterVO.getSearchBl());
	    	}
	    	
	    	String excelTitle = "SAP 전표 조회";
	    	if ((searchBlNo == null) || (searchBlNo.equals(""))) {
	    		excelTitle = "SAP 전표 조회_"+searchBgnDe1+"_"+searchEndDe1;
			}
			else {
				excelTitle = "SAP 전표 상세조회_"+searchBlNo;
			}
			
			
			List<BoardMasterVO> lstResult = bbsAttrbService.selectSapList(boardMasterVO);
	
			boolean excelOption = false;
			
			//첫Row,마지막Row,첫cell,마지막cell, row높이, 스타일, 내용
			Map<String, Object> headerMap = new HashMap<String, Object>();
			
			headerMap.put("sRow", "1");
			headerMap.put("eRow", "1");
			headerMap.put("sCol", "0");
			headerMap.put("eCol", "10");
			headerMap.put("fontType", "titleLine");
			headerMap.put("fontColor", "000000");
			headerMap.put("styleColor", "FFFFFF");
			headerMap.put("textAlign", "center");
			headerMap.put("textVAlign", "center");
			headerMap.put("line", "none");
			headerMap.put("title", "SAP 전표 조회");
			
			//첫Row,마지막Row,첫cell,마지막cell, row높이, 스타일, 내용
			Map<String, Object> unitMap = new HashMap<String, Object>();
			
			unitMap.put("sRow", "2");
			unitMap.put("eRow", "2");
			unitMap.put("sCol", "0");
			unitMap.put("eCol", "10");
			unitMap.put("fontType", "unitLine");
			unitMap.put("fontColor", "000000");
			unitMap.put("styleColor", "FFFFFF");
			unitMap.put("textAlign", "right");
			unitMap.put("textVAlign", "center");
			unitMap.put("line", "none");
			if ((searchBlNo == null) || (searchBlNo.equals(""))) {
				unitMap.put("title", "B/L 조회일자 : " + searchBgnDe + " ~ " + searchEndDe);
				excelOption = true;
			}
			else {
				unitMap.put("title", "B/L No : " + searchBlNo);
			}
			
			Map<String, Object> titleStyleMap = new HashMap<String, Object>();
			
			titleStyleMap.put("sRow", "3");
			titleStyleMap.put("eRow", "3");
			titleStyleMap.put("sCol", "0");
			titleStyleMap.put("eCol", "10");
			titleStyleMap.put("fontType", "subtitle");
			titleStyleMap.put("fontColor", "000000");
			titleStyleMap.put("styleColor", "ECF5FC");
			titleStyleMap.put("textAlign", "center");
			titleStyleMap.put("textVAlign", "center");
			titleStyleMap.put("line", "line");
			
			
			List<Map<String, Object>> fieldInfoList = new ArrayList<Map<String, Object>>();
			Map<String, Object> fieldInfoMap = new HashMap<String, Object>();
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "bktxt");
			fieldInfoMap.put("cellTitle", "B/L 번호");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 15*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			/*
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "zfimdno");
			fieldInfoMap.put("cellTitle", "수입문서번호");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 21*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "cond_type");
			fieldInfoMap.put("cellTitle", "조건유형");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			*/
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "vtext");
			fieldInfoMap.put("cellTitle", "조건유형 텍스트");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 25*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "left");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "wrbtr");
			fieldInfoMap.put("cellTitle", "비용");
			fieldInfoMap.put("fileType", "Int");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "right");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "* #,##0_-;-* #,##0_-;_-* \"-\"_-;_-@_-");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "waers");
			fieldInfoMap.put("cellTitle", "통화");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			/*
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "zfacdo");
			fieldInfoMap.put("cellTitle", "회계전표번호");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			*/
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "belnr");
			fieldInfoMap.put("cellTitle", "전표번호");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "bldat");
			fieldInfoMap.put("cellTitle", "증빙일");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "budat");
			fieldInfoMap.put("cellTitle", "전기일");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "lifnr");
			fieldInfoMap.put("cellTitle", "공급업체");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "name1");
			fieldInfoMap.put("cellTitle", "공급업체명");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 30*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "left");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			/*
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "zfcstgrp");
			fieldInfoMap.put("cellTitle", "비용그룹");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			*/
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "mwskz");
			fieldInfoMap.put("cellTitle", "세액코드");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "wmwst");
			fieldInfoMap.put("cellTitle", "세액");
			fieldInfoMap.put("fileType", "Int");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "right");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "* #,##0_-;-* #,##0_-;_-* \"-\"_-;_-@_-");
			fieldInfoList.add(fieldInfoMap);
			/*
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "zfcd");
			fieldInfoMap.put("cellTitle", "관리코드");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "n16");
			fieldInfoMap.put("cellTitle", "지급조건");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			*/
					
			Map<String, Object> excelInfpMap = new HashMap<String, Object>();
			excelInfpMap.put("headerMap", headerMap);
			excelInfpMap.put("unitMap", unitMap);			
			excelInfpMap.put("titleStyleMap", titleStyleMap);
			excelInfpMap.put("fieldInfoList", fieldInfoList);
			
			
			SXSSFWorkbook workbook = bbsAttrbService.buildExcelXSS(excelTitle,excelInfpMap, lstResult, excelOption);

			String realExcelFilename = null;
			realExcelFilename = URLEncoder.encode(excelTitle, "UTF-8");
			realExcelFilename = realExcelFilename.replaceAll("\\+", " ");

			/*
			 * HTTP Header 설정.
			 */
			response.setContentType("application/vnd.ms-excel; name=\"" + realExcelFilename + ".xlsx\"");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + realExcelFilename + ".xlsx\"");
			response.setHeader("Content-Transfer-Encoding", "binary");
//            response.setHeader("Content-Length", Long.toString(fileDownLoadInputVO.getlFileSize()));
			response.setHeader("Cache-Control", "no-cahe, no-store, must-revalidate\r\n");
			response.setHeader("Connection", "close");

			//FileOutputStream fileOut = new FileOutputStream(realExcelFilename + ".xlsx");
			//OutputStream out = response.getOutputStream();
			OutputStream fileOut = response.getOutputStream();

			workbook.write(fileOut);
			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}


		return returnMap;
    }
	
	
	/**
     *RPA 목록을 조회한다.
     */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@ResponseBody
    @RequestMapping("/cop/bbs/selectSapSubListExcel.do")
    public Map<Object, Object> selectSapSubListExcel(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	Map<Object, Object> returnMap = new HashMap<Object, Object>();
    	
		try {
	    	String searchBlNo = boardMasterVO.getSearchBlNo();
	    	
	    	if ((boardMasterVO.getSearchBl() != null) && !(boardMasterVO.getSearchBl().equals(""))) {
	        	
	    		boardMasterVO.setSearchBlNo(boardMasterVO.getSearchBl());
	    	}
	    	
	    	String excelTitle = "SAP 전표 상세조회_"+searchBlNo;
			
			Map<String, Object> map = bbsAttrbService.selectSapSubBLList(boardMasterVO);
	    	
			List<BoardMasterVO> lstResult = (List<BoardMasterVO>) map.get("resultList");
	
			//첫Row,마지막Row,첫cell,마지막cell, row높이, 스타일, 내용
			Map<String, Object> headerMap = new HashMap<String, Object>();
			
			headerMap.put("sRow", "1");
			headerMap.put("eRow", "1");
			headerMap.put("sCol", "0");
			headerMap.put("eCol", "10");
			headerMap.put("fontType", "titleLine");
			headerMap.put("fontColor", "000000");
			headerMap.put("styleColor", "FFFFFF");
			headerMap.put("textAlign", "center");
			headerMap.put("textVAlign", "center");
			headerMap.put("line", "none");
			headerMap.put("title", "SAP 전표 상세조회");
			
			//첫Row,마지막Row,첫cell,마지막cell, row높이, 스타일, 내용
			Map<String, Object> unitMap = new HashMap<String, Object>();
			
			unitMap.put("sRow", "2");
			unitMap.put("eRow", "2");
			unitMap.put("sCol", "0");
			unitMap.put("eCol", "10");
			unitMap.put("fontType", "unitLine");
			unitMap.put("fontColor", "000000");
			unitMap.put("styleColor", "FFFFFF");
			unitMap.put("textAlign", "right");
			unitMap.put("textVAlign", "center");
			unitMap.put("line", "none");
			unitMap.put("title", "B/L No : " + searchBlNo);
			
			Map<String, Object> titleStyleMap = new HashMap<String, Object>();
			
			titleStyleMap.put("sRow", "3");
			titleStyleMap.put("eRow", "3");
			titleStyleMap.put("sCol", "0");
			titleStyleMap.put("eCol", "10");
			titleStyleMap.put("fontType", "subtitle");
			titleStyleMap.put("fontColor", "000000");
			titleStyleMap.put("styleColor", "ECF5FC");
			titleStyleMap.put("textAlign", "center");
			titleStyleMap.put("textVAlign", "center");
			titleStyleMap.put("line", "line");
			
			
			List<Map<String, Object>> fieldInfoList = new ArrayList<Map<String, Object>>();
			Map<String, Object> fieldInfoMap = new HashMap<String, Object>();
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "bktxt");
			fieldInfoMap.put("cellTitle", "B/L 번호");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 15*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			/*
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "zfimdno");
			fieldInfoMap.put("cellTitle", "수입문서번호");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 21*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "cond_type");
			fieldInfoMap.put("cellTitle", "조건유형");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			*/
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "vtext");
			fieldInfoMap.put("cellTitle", "조건유형 텍스트");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "wrbtr");
			fieldInfoMap.put("cellTitle", "비용");
			fieldInfoMap.put("fileType", "Int");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "right");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "* #,##0_-;-* #,##0_-;_-* \"-\"_-;_-@_-");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "waers");
			fieldInfoMap.put("cellTitle", "통화");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			/*
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "zfacdo");
			fieldInfoMap.put("cellTitle", "회계전표번호");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			*/
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "belnr");
			fieldInfoMap.put("cellTitle", "전표번호");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "bldat");
			fieldInfoMap.put("cellTitle", "증빙일");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "budat");
			fieldInfoMap.put("cellTitle", "전기일");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "lifnr");
			fieldInfoMap.put("cellTitle", "공급업체");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "name1");
			fieldInfoMap.put("cellTitle", "공급업체명");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			/*
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "zfcstgrp");
			fieldInfoMap.put("cellTitle", "비용그룹");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			*/
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "mwskz");
			fieldInfoMap.put("cellTitle", "세액코드");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "wmwst");
			fieldInfoMap.put("cellTitle", "세액");
			fieldInfoMap.put("fileType", "Int");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "right");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "* #,##0_-;-* #,##0_-;_-* \"-\"_-;_-@_-");
			fieldInfoList.add(fieldInfoMap);
			/*
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "zfcd");
			fieldInfoMap.put("cellTitle", "관리코드");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "n16");
			fieldInfoMap.put("cellTitle", "지급조건");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoMap.put("fontType", "content");
			fieldInfoMap.put("fontColor", "000000");
			fieldInfoMap.put("styleColor", "FFFFFF");
			fieldInfoMap.put("textAlign", "center");
			fieldInfoMap.put("textVAlign", "center");
			fieldInfoMap.put("line", "dot");
			fieldInfoMap.put("fomule", "");
			fieldInfoList.add(fieldInfoMap);
			*/
					
			Map<String, Object> excelInfpMap = new HashMap<String, Object>();
			excelInfpMap.put("headerMap", headerMap);
			excelInfpMap.put("unitMap", unitMap);			
			excelInfpMap.put("titleStyleMap", titleStyleMap);
			excelInfpMap.put("fieldInfoList", fieldInfoList);
			
			
			SXSSFWorkbook workbook = bbsAttrbService.buildExcelSubXSS(excelTitle,excelInfpMap, lstResult);

			String realExcelFilename = null;
			realExcelFilename = URLEncoder.encode(excelTitle, "UTF-8");
			realExcelFilename = realExcelFilename.replaceAll("\\+", " ");

			/*
			 * HTTP Header 설정.
			 */
			response.setContentType("application/vnd.ms-excel; name=\"" + realExcelFilename + ".xlsx\"");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + realExcelFilename + ".xlsx\"");
			response.setHeader("Content-Transfer-Encoding", "binary");
//            response.setHeader("Content-Length", Long.toString(fileDownLoadInputVO.getlFileSize()));
			response.setHeader("Cache-Control", "no-cahe, no-store, must-revalidate\r\n");
			response.setHeader("Connection", "close");

			//FileOutputStream fileOut = new FileOutputStream(realExcelFilename + ".xlsx");
			//OutputStream out = response.getOutputStream();
			OutputStream fileOut = response.getOutputStream();

			workbook.write(fileOut);
			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}


		return returnMap;
    }
    
    /**
     *RPA 목록을 조회한다.
     */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@ResponseBody
    @RequestMapping("/cop/bbs/selectExtraSPExcel.do")
    public Map<Object, Object> selectExtraSPExcel(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	Map<Object, Object> returnMap = new HashMap<Object, Object>();
    	
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM", Locale.KOREA);
			SimpleDateFormat formatter1= new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
			Calendar cal = Calendar.getInstance();
	    	cal.add(Calendar.MONTH , -1);
	
	    	String firstDate = boardMasterVO.getSearchMonth();
	    	 
	    	if ((boardMasterVO.getSearchMonth() == null) || (boardMasterVO.getSearchMonth().equals(""))) {
	    		cal.set(Calendar.DATE, 1);
	    		 
	    	    firstDate = formatter.format(cal.getTime());
	    	    
	    		boardMasterVO.setSearchMonth(firstDate);
	    	}
	    	else {
	    		Date tempDate = formatter1.parse(firstDate+"01");

	    		cal.set(tempDate.getYear(), tempDate.getMonth()+1, tempDate.getDay());
	    	}
	    	
	    	int year3 = cal.get(Calendar.YEAR); 
	        int month3 = cal.get(Calendar.MONTH); 
	        int day3 = cal.get(Calendar.DATE);
	    	
	    	int nMonth = month3 + 1;
	    	
	
			String excelTitle = nMonth + "월 수입 부대비용 마감 내역";
			//String sheetTitle = "extraprice";
			//String headerTitle = nMonth + "월 수입 부대비용 마감 내역";
	
			//SimpleDateFormat formatter2 = new SimpleDateFormat( "yyyy-MM-dd", Locale.KOREA );
			//Date today = new Date();
			//String dToday = formatter1.format(today);
	
			List<BoardMasterVO> lstResult = null;
			List<BoardMasterVO> lstResult1 = null;
			
			Map<String,Object> map =  bbsAttrbService.selectExtraSPList1(boardMasterVO);
	    	
			lstResult = (List<BoardMasterVO>) map.get("extraList");
			lstResult1 = (List<BoardMasterVO>) map.get("extra1List");
			long total = Long.parseLong(map.get("resultTotal").toString());
			
			String firstListEnd = Long.toString(lstResult.size() + 4);
			
			String nextHeadStart = Long.toString(Long.parseLong(firstListEnd) + 23);
			
			String nextTitleStart = Long.toString(Long.parseLong(nextHeadStart) + 2);
			String nextTitleEnd = Long.toString(Long.parseLong(nextHeadStart) + 3);
			
			String nextListStart = Long.toString(Long.parseLong(nextTitleEnd) + 1);
			String nextListEnd = Long.toString(Long.parseLong(nextTitleEnd) + lstResult1.size() + 1);
	
			
			//첫Row,마지막Row,첫cell,마지막cell, row높이, 스타일, 내용
			Map<String, Object> headerMap = new HashMap<String, Object>();
			
			headerMap.put("sRow", "1");
			headerMap.put("eRow", "1");
			headerMap.put("sRow1", nextHeadStart);
			headerMap.put("eRow1", nextHeadStart);
			headerMap.put("sCol", "0");
			headerMap.put("eCol", "5");
			headerMap.put("fontType", "titleLine");
			headerMap.put("fontColor", "000000");
			headerMap.put("styleColor", "FFFFFF");
			headerMap.put("textAlign", "center");
			headerMap.put("textVAlign", "center");
			headerMap.put("line", "none");
			headerMap.put("title", nMonth + "월 수입 부대비용 마감 내역");
			headerMap.put("title1", nMonth + "월 수입 부대비용 개별 마감업체 내역");
			
			
			Map<String, Object> titleStyleMap = new HashMap<String, Object>();
			
			titleStyleMap.put("sRow", "3");
			titleStyleMap.put("eRow", "4");
			titleStyleMap.put("sRow1", nextTitleStart);
			titleStyleMap.put("eRow1", nextTitleEnd);
			titleStyleMap.put("sCol", "0");
			titleStyleMap.put("eCol", "5");
			titleStyleMap.put("fontType", "subtitle");
			titleStyleMap.put("fontColor", "000000");
			titleStyleMap.put("styleColor", "ECF5FC");
			titleStyleMap.put("textAlign", "center");
			titleStyleMap.put("textVAlign", "center");
			titleStyleMap.put("line", "line");
			titleStyleMap.put("firstRow", "5");
			titleStyleMap.put("lastRow", firstListEnd);
			titleStyleMap.put("firstRow1", nextListStart);
			titleStyleMap.put("lastRow1", nextListEnd);
			
			List<Map<String, Object>> titleCellList = new ArrayList<Map<String, Object>>();
			Map<String, Object> titleCellMap = new HashMap<String, Object>();
			
			titleCellMap.put("sRow", "3");
			titleCellMap.put("eRow", "4");
			titleCellMap.put("sCol", "0");
			titleCellMap.put("eCol", "0");
			titleCellMap.put("cellTitle", "업체");
			titleCellList.add(titleCellMap);
			
			titleCellMap = new HashMap<String, Object>();
			titleCellMap.put("sRow", "3");
			titleCellMap.put("eRow", "4");
			titleCellMap.put("sCol", "1");
			titleCellMap.put("eCol", "1");
			titleCellMap.put("cellTitle", "세부내역");
			titleCellList.add(titleCellMap);
			
			titleCellMap = new HashMap<String, Object>();
			titleCellMap.put("sRow", "3");
			titleCellMap.put("eRow", "4");
			titleCellMap.put("sCol", "2");
			titleCellMap.put("eCol", "2");
			titleCellMap.put("cellTitle", "관세사");
			titleCellList.add(titleCellMap);
			
			titleCellMap = new HashMap<String, Object>();
			titleCellMap.put("sRow", "3");
			titleCellMap.put("eRow", "3");
			titleCellMap.put("sCol", "3");
			titleCellMap.put("eCol", "5");
			titleCellMap.put("cellTitle", "금액(\\)");
			titleCellList.add(titleCellMap);
			
			titleCellMap = new HashMap<String, Object>();
			titleCellMap.put("sRow", "4");
			titleCellMap.put("eRow", "4");
			titleCellMap.put("sCol", "3");
			titleCellMap.put("eCol", "3");
			titleCellMap.put("cellTitle", "공급가액");
			titleCellList.add(titleCellMap);
			
			titleCellMap = new HashMap<String, Object>();
			titleCellMap.put("sRow", "4");
			titleCellMap.put("eRow", "4");
			titleCellMap.put("sCol", "4");
			titleCellMap.put("eCol", "4");
			titleCellMap.put("cellTitle", "부가세");
			titleCellList.add(titleCellMap);
			
			titleCellMap = new HashMap<String, Object>();
			titleCellMap.put("sRow", "4");
			titleCellMap.put("eRow", "4");
			titleCellMap.put("sCol", "5");
			titleCellMap.put("eCol", "5");
			titleCellMap.put("cellTitle", "Total");
			titleCellList.add(titleCellMap);
			
			
			List<Map<String, Object>> fieldInfoList = new ArrayList<Map<String, Object>>();
			Map<String, Object> fieldInfoMap = new HashMap<String, Object>();
			
			fieldInfoMap.put("field", "extra_company");
			fieldInfoMap.put("field1", "extra1_company");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 23*256);
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "extra_detail");
			fieldInfoMap.put("field1", "extra1_detail");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 15*256);
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "extra_custom");
			fieldInfoMap.put("field1", "extra1_custom");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 21*256);
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "extra_price");
			fieldInfoMap.put("field1", "extra1_price");
			fieldInfoMap.put("fileType", "Int");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "extra_tax");
			fieldInfoMap.put("field1", "extra1_tax");
			fieldInfoMap.put("fileType", "Int");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "extra_total");
			fieldInfoMap.put("field1", "extra1_total");
			fieldInfoMap.put("fileType", "Int");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoList.add(fieldInfoMap);
			
			List<Map<String, Object>> fieldStyleList = new ArrayList<Map<String, Object>>();
			Map<String, Object> fieldStyleMap = new HashMap<String, Object>();
			
			fieldStyleMap.put("fontType", "content");
			fieldStyleMap.put("fontColor", "000000");
			fieldStyleMap.put("styleColor", "FFFFFF");
			fieldStyleMap.put("textAlign", "center");
			fieldStyleMap.put("textVAlign", "center");
			fieldStyleMap.put("line", "dot");
			fieldStyleMap.put("fomule", "");
			fieldStyleList.add(fieldStyleMap);
			
			fieldStyleMap = new HashMap<String, Object>();
			fieldStyleMap.put("fontType", "content");
			fieldStyleMap.put("fontColor", "000000");
			fieldStyleMap.put("styleColor", "FFFFFF");
			fieldStyleMap.put("textAlign", "center");
			fieldStyleMap.put("textVAlign", "center");
			fieldStyleMap.put("line", "dot");
			fieldStyleMap.put("fomule", "");
			fieldStyleList.add(fieldStyleMap);
			
			fieldStyleMap = new HashMap<String, Object>();
			fieldStyleMap.put("fontType", "content");
			fieldStyleMap.put("fontColor", "000000");
			fieldStyleMap.put("styleColor", "FFFFFF");
			fieldStyleMap.put("textAlign", "center");
			fieldStyleMap.put("textVAlign", "center");
			fieldStyleMap.put("line", "dot");
			fieldStyleMap.put("fomule", "");
			fieldStyleList.add(fieldStyleMap);
			
			fieldStyleMap = new HashMap<String, Object>();
			fieldStyleMap.put("fontType", "content");
			fieldStyleMap.put("fontColor", "000000");
			fieldStyleMap.put("styleColor", "FFFFFF");
			fieldStyleMap.put("textAlign", "right");
			fieldStyleMap.put("textVAlign", "center");
			fieldStyleMap.put("line", "dot");
			fieldStyleMap.put("fomule", "* #,##0_-;-* #,##0_-;_-* \"-\"_-;_-@_-");
			fieldStyleList.add(fieldStyleMap);
			
			fieldStyleMap = new HashMap<String, Object>();
			fieldStyleMap.put("fontType", "content");
			fieldStyleMap.put("fontColor", "000000");
			fieldStyleMap.put("styleColor", "FFFFFF");
			fieldStyleMap.put("textAlign", "right");
			fieldStyleMap.put("textVAlign", "center");
			fieldStyleMap.put("line", "dot");
			fieldStyleMap.put("fomule", "* #,##0_-;-* #,##0_-;_-* \"-\"_-;_-@_-");
			fieldStyleList.add(fieldStyleMap);
			
			fieldStyleMap = new HashMap<String, Object>();
			fieldStyleMap.put("fontType", "content");
			fieldStyleMap.put("fontColor", "000000");
			fieldStyleMap.put("styleColor", "FFFFFF");
			fieldStyleMap.put("textAlign", "right");
			fieldStyleMap.put("textVAlign", "center");
			fieldStyleMap.put("line", "dot");
			fieldStyleMap.put("fomule", "* #,##0_-;-* #,##0_-;_-* \"-\"_-;_-@_-");
			fieldStyleList.add(fieldStyleMap);
					
			Map<String, Object> excelInfpMap = new HashMap<String, Object>();
			excelInfpMap.put("headerMap", headerMap);
			excelInfpMap.put("titleStyleMap", titleStyleMap);
			excelInfpMap.put("titleCellList", titleCellList);
			excelInfpMap.put("fieldInfoList", fieldInfoList);
			excelInfpMap.put("fieldStyleList", fieldStyleList);
			
			
			SXSSFWorkbook workbook = bbsAttrbService.buildExcelMergeXSS(excelTitle,excelInfpMap, lstResult,lstResult1,total,nMonth);

			String realExcelFilename = null;
			realExcelFilename = URLEncoder.encode(excelTitle, "UTF-8");
			realExcelFilename = realExcelFilename.replaceAll("\\+", " ");

			/*
			 * HTTP Header 설정.
			 */
			response.setContentType("application/vnd.ms-excel; name=\"" + realExcelFilename + ".xlsx\"");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + realExcelFilename + ".xlsx\"");
			response.setHeader("Content-Transfer-Encoding", "binary");
//            response.setHeader("Content-Length", Long.toString(fileDownLoadInputVO.getlFileSize()));
			response.setHeader("Cache-Control", "no-cahe, no-store, must-revalidate\r\n");
			response.setHeader("Connection", "close");

			//FileOutputStream fileOut = new FileOutputStream(realExcelFilename + ".xlsx");
			//OutputStream out = response.getOutputStream();
			OutputStream fileOut = response.getOutputStream();

			workbook.write(fileOut);
			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}


		return returnMap;
    }
	
	/**
     *RPA 목록을 조회한다.
     */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@ResponseBody
    @RequestMapping("/cop/bbs/selectExtraSPExcel1.do")
    public Map<Object, Object> selectExtraSPExcel1(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	Map<Object, Object> returnMap = new HashMap<Object, Object>();
    	
		try {
			SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd", Locale.KOREA );
			SimpleDateFormat formatter1 = new SimpleDateFormat( "yyyyMMdd", Locale.KOREA );
			Calendar cal = Calendar.getInstance();
	    	
	    	String searchGubun = boardMasterVO.getSearchGubun();
	    	String searchBgnDe = boardMasterVO.getSearchBgnDe();
	    	String searchEndDe = boardMasterVO.getSearchEndDe();
	    	String searchBgnDe1 = boardMasterVO.getSearchBgnDe();
	    	String searchEndDe1 = boardMasterVO.getSearchEndDe();
	    	
	    	if ((boardMasterVO.getSearchGubun() == null) || (boardMasterVO.getSearchGubun().equals(""))) {
	        	boardMasterVO.setSearchGubun("BL");
	    	} 
	    	if ((boardMasterVO.getSearchBgnDe() == null) || (boardMasterVO.getSearchBgnDe().equals(""))) {
	        	cal.set(Calendar.DATE, 1);
	       	 
	        	searchBgnDe = formatter.format(cal.getTime());
	        	searchBgnDe1 = formatter1.format(cal.getTime());
	    	}
	    	else {
	    		Date tempDate = formatter1.parse(searchBgnDe);
	    		searchBgnDe = formatter.format(tempDate);
	    		
	    	}
	    	if ((boardMasterVO.getSearchEndDe() == null) || (boardMasterVO.getSearchEndDe().equals(""))) {
	        	cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
	        	
	        	searchEndDe = formatter.format(cal.getTime());
	        	searchEndDe1 = formatter1.format(cal.getTime());
	    	}
	    	else {
	    		Date tempDate = formatter1.parse(searchEndDe);
	    		searchEndDe = formatter.format(tempDate);
	    	}
	    	
	
			String excelTitle = "수입 부대비용 마감 내역_"+searchBgnDe1+"_"+searchEndDe1;
	    	if (searchGubun.equals("BL")) {
	    		excelTitle = "수입 부대비용 마감 내역_BL일 기준_"+searchBgnDe1+"_"+searchEndDe1;
			}
			else {
				excelTitle = "수입 부대비용 마감 내역_증빙일 기준_"+searchBgnDe1+"_"+searchEndDe1;
			}
			//String sheetTitle = "extraprice";
			//String headerTitle = nMonth + "월 수입 부대비용 마감 내역";
	
			//SimpleDateFormat formatter2 = new SimpleDateFormat( "yyyy-MM-dd", Locale.KOREA );
			//Date today = new Date();
			//String dToday = formatter1.format(today);
	
			List<BoardMasterVO> lstResult = bbsAttrbService.selectExtraSPFinishList(boardMasterVO);
			
			String firstListEnd = Long.toString(lstResult.size() + 4);
			
			String nextHeadStart = Long.toString(Long.parseLong(firstListEnd) + 23);
			
			//첫Row,마지막Row,첫cell,마지막cell, row높이, 스타일, 내용
			Map<String, Object> headerMap = new HashMap<String, Object>();
			
			headerMap.put("sRow", "1");
			headerMap.put("eRow", "1");
			headerMap.put("sRow1", nextHeadStart);
			headerMap.put("eRow1", nextHeadStart);
			headerMap.put("sCol", "0");
			headerMap.put("eCol", "5");
			headerMap.put("fontType", "titleLine");
			headerMap.put("fontColor", "000000");
			headerMap.put("styleColor", "FFFFFF");
			headerMap.put("textAlign", "center");
			headerMap.put("textVAlign", "center");
			headerMap.put("line", "none");
			headerMap.put("title", "수입 부대비용 마감 내역");
			
			
			//첫Row,마지막Row,첫cell,마지막cell, row높이, 스타일, 내용
			Map<String, Object> unitMap = new HashMap<String, Object>();
			
			unitMap.put("sRow", "2");
			unitMap.put("eRow", "2");
			unitMap.put("sCol", "0");
			unitMap.put("eCol", "5");
			unitMap.put("fontType", "unitLine");
			unitMap.put("fontColor", "000000");
			unitMap.put("styleColor", "FFFFFF");
			unitMap.put("textAlign", "right");
			unitMap.put("textVAlign", "center");
			unitMap.put("line", "none");
			if (searchGubun.equals("BL")) {
				unitMap.put("title", "B/L 조회일자 : " + searchBgnDe + " ~ " + searchEndDe);
			}
			else {
				unitMap.put("title", "증빙일 조회일자 : " + searchBgnDe + " ~ " + searchEndDe);
			}
			
			Map<String, Object> titleStyleMap = new HashMap<String, Object>();
			
			titleStyleMap.put("sRow", "3");
			titleStyleMap.put("eRow", "4");
			titleStyleMap.put("sCol", "0");
			titleStyleMap.put("eCol", "5");
			titleStyleMap.put("fontType", "subtitle");
			titleStyleMap.put("fontColor", "000000");
			titleStyleMap.put("styleColor", "ECF5FC");
			titleStyleMap.put("textAlign", "center");
			titleStyleMap.put("textVAlign", "center");
			titleStyleMap.put("line", "line");
			titleStyleMap.put("firstRow", "5");
			titleStyleMap.put("lastRow", firstListEnd);
			
			List<Map<String, Object>> titleCellList = new ArrayList<Map<String, Object>>();
			Map<String, Object> titleCellMap = new HashMap<String, Object>();
			
			titleCellMap.put("sRow", "3");
			titleCellMap.put("eRow", "4");
			titleCellMap.put("sCol", "0");
			titleCellMap.put("eCol", "0");
			titleCellMap.put("cellTitle", "관세사");
			titleCellList.add(titleCellMap);
			
			titleCellMap = new HashMap<String, Object>();
			titleCellMap.put("sRow", "3");
			titleCellMap.put("eRow", "4");
			titleCellMap.put("sCol", "1");
			titleCellMap.put("eCol", "1");
			titleCellMap.put("cellTitle", "업체");
			titleCellList.add(titleCellMap);
			
			titleCellMap = new HashMap<String, Object>();
			titleCellMap.put("sRow", "3");
			titleCellMap.put("eRow", "4");
			titleCellMap.put("sCol", "2");
			titleCellMap.put("eCol", "2");
			titleCellMap.put("cellTitle", "세부내역");
			titleCellList.add(titleCellMap);
			
			titleCellMap = new HashMap<String, Object>();
			titleCellMap.put("sRow", "3");
			titleCellMap.put("eRow", "3");
			titleCellMap.put("sCol", "3");
			titleCellMap.put("eCol", "5");
			titleCellMap.put("cellTitle", "금액(\\)");
			titleCellList.add(titleCellMap);
			
			titleCellMap = new HashMap<String, Object>();
			titleCellMap.put("sRow", "4");
			titleCellMap.put("eRow", "4");
			titleCellMap.put("sCol", "3");
			titleCellMap.put("eCol", "3");
			titleCellMap.put("cellTitle", "공급가액");
			titleCellList.add(titleCellMap);
			
			titleCellMap = new HashMap<String, Object>();
			titleCellMap.put("sRow", "4");
			titleCellMap.put("eRow", "4");
			titleCellMap.put("sCol", "4");
			titleCellMap.put("eCol", "4");
			titleCellMap.put("cellTitle", "부가세");
			titleCellList.add(titleCellMap);
			
			titleCellMap = new HashMap<String, Object>();
			titleCellMap.put("sRow", "4");
			titleCellMap.put("eRow", "4");
			titleCellMap.put("sCol", "5");
			titleCellMap.put("eCol", "5");
			titleCellMap.put("cellTitle", "Total");
			titleCellList.add(titleCellMap);
			
			
			List<Map<String, Object>> fieldInfoList = new ArrayList<Map<String, Object>>();
			Map<String, Object> fieldInfoMap = new HashMap<String, Object>();
			
			fieldInfoMap.put("field", "extra_custom");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 21*256);
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "extra_company");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 23*256);
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "extra_detail");
			fieldInfoMap.put("fileType", "String");
			fieldInfoMap.put("cellWidth", 15*256);
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "extra_price");
			fieldInfoMap.put("fileType", "Int");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "extra_tax");
			fieldInfoMap.put("fileType", "Int");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoList.add(fieldInfoMap);
			
			fieldInfoMap = new HashMap<String, Object>();
			fieldInfoMap.put("field", "extra_total");
			fieldInfoMap.put("fileType", "Int");
			fieldInfoMap.put("cellWidth", 13*256);
			fieldInfoList.add(fieldInfoMap);
			
			List<Map<String, Object>> fieldStyleList = new ArrayList<Map<String, Object>>();
			Map<String, Object> fieldStyleMap = new HashMap<String, Object>();
			
			fieldStyleMap.put("fontType", "content");
			fieldStyleMap.put("fontColor", "000000");
			fieldStyleMap.put("styleColor", "FFFFFF");
			fieldStyleMap.put("textAlign", "center");
			fieldStyleMap.put("textVAlign", "center");
			fieldStyleMap.put("line", "dot");
			fieldStyleMap.put("fomule", "");
			fieldStyleList.add(fieldStyleMap);
			
			fieldStyleMap = new HashMap<String, Object>();
			fieldStyleMap.put("fontType", "content");
			fieldStyleMap.put("fontColor", "000000");
			fieldStyleMap.put("styleColor", "FFFFFF");
			fieldStyleMap.put("textAlign", "center");
			fieldStyleMap.put("textVAlign", "center");
			fieldStyleMap.put("line", "dot");
			fieldStyleMap.put("fomule", "");
			fieldStyleList.add(fieldStyleMap);
			
			fieldStyleMap = new HashMap<String, Object>();
			fieldStyleMap.put("fontType", "content");
			fieldStyleMap.put("fontColor", "000000");
			fieldStyleMap.put("styleColor", "FFFFFF");
			fieldStyleMap.put("textAlign", "center");
			fieldStyleMap.put("textVAlign", "center");
			fieldStyleMap.put("line", "dot");
			fieldStyleMap.put("fomule", "");
			fieldStyleList.add(fieldStyleMap);
			
			fieldStyleMap = new HashMap<String, Object>();
			fieldStyleMap.put("fontType", "content");
			fieldStyleMap.put("fontColor", "000000");
			fieldStyleMap.put("styleColor", "FFFFFF");
			fieldStyleMap.put("textAlign", "right");
			fieldStyleMap.put("textVAlign", "center");
			fieldStyleMap.put("line", "dot");
			fieldStyleMap.put("fomule", "* #,##0_-;-* #,##0_-;_-* \"-\"_-;_-@_-");
			fieldStyleList.add(fieldStyleMap);
			
			fieldStyleMap = new HashMap<String, Object>();
			fieldStyleMap.put("fontType", "content");
			fieldStyleMap.put("fontColor", "000000");
			fieldStyleMap.put("styleColor", "FFFFFF");
			fieldStyleMap.put("textAlign", "right");
			fieldStyleMap.put("textVAlign", "center");
			fieldStyleMap.put("line", "dot");
			fieldStyleMap.put("fomule", "* #,##0_-;-* #,##0_-;_-* \"-\"_-;_-@_-");
			fieldStyleList.add(fieldStyleMap);
			
			fieldStyleMap = new HashMap<String, Object>();
			fieldStyleMap.put("fontType", "content");
			fieldStyleMap.put("fontColor", "000000");
			fieldStyleMap.put("styleColor", "FFFFFF");
			fieldStyleMap.put("textAlign", "right");
			fieldStyleMap.put("textVAlign", "center");
			fieldStyleMap.put("line", "dot");
			fieldStyleMap.put("fomule", "* #,##0_-;-* #,##0_-;_-* \"-\"_-;_-@_-");
			fieldStyleList.add(fieldStyleMap);
					
			Map<String, Object> excelInfpMap = new HashMap<String, Object>();
			excelInfpMap.put("headerMap", headerMap);
			excelInfpMap.put("unitMap", unitMap);
			excelInfpMap.put("titleStyleMap", titleStyleMap);
			excelInfpMap.put("titleCellList", titleCellList);
			excelInfpMap.put("fieldInfoList", fieldInfoList);
			excelInfpMap.put("fieldStyleList", fieldStyleList);
			
			
			SXSSFWorkbook workbook = bbsAttrbService.buildExcelMergeXSS1(excelTitle,excelInfpMap, lstResult);

			String realExcelFilename = null;
			realExcelFilename = URLEncoder.encode(excelTitle, "UTF-8");
			realExcelFilename = realExcelFilename.replaceAll("\\+", " ");

			/*
			 * HTTP Header 설정.
			 */
			response.setContentType("application/vnd.ms-excel; name=\"" + realExcelFilename + ".xlsx\"");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + realExcelFilename + ".xlsx\"");
			response.setHeader("Content-Transfer-Encoding", "binary");
//            response.setHeader("Content-Length", Long.toString(fileDownLoadInputVO.getlFileSize()));
			response.setHeader("Cache-Control", "no-cahe, no-store, must-revalidate\r\n");
			response.setHeader("Connection", "close");

			//FileOutputStream fileOut = new FileOutputStream(realExcelFilename + ".xlsx");
			//OutputStream out = response.getOutputStream();
			OutputStream fileOut = response.getOutputStream();

			workbook.write(fileOut);
			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}


		return returnMap;
    }
    
    /**
     *RPA 목록을 조회한다.
     */
    @RequestMapping("/cop/bbs/popupForward.do")
    public String popupForward(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
    	
    	if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
    	
		return "cop/bbs/EgovBoardBLReg";
    }
    
    /**
     *RPA 목록을 조회한다.
     */
    @RequestMapping("/cop/bbs/uploadSapFile.do")
    public String uploadSapFile(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
    	
    	if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
    	
		return "cop/bbs/EgovBoardSapFile";
    }
    
    @RequestMapping(value = "/file/fileUpDownloadSap.do")
    public String daumEditor(ModelMap model) throws Exception {
    	
              return "copycoding/util/FileUpDownload";
    }

    

    @RequestMapping(value = "/file/fileUploadSap.do")
    @ResponseBody
    public Map<String, Object> uploadSingleFile(
                         MultipartHttpServletRequest multiRequest,
                         HttpServletRequest request,
                         SessionVO sessionVO,
                         ModelMap model,
                         SessionStatus status) throws Exception{
              
    	//파일이 생성되고나면 생성된 첨부파일 ID를 리턴한다.
    	int process = bbsAttrbService.insertFileSapInf(multiRequest,request);  
        
     // 요청에 응답하기 위한 맵 객체를 성한다.
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (process > -1) {
            resultMap.put("status", 0);
            resultMap.put("msg", "정상적으로 파일이 등록되었습니다.");
        }
        else{
            resultMap.put("status", 1);
            resultMap.put("msg", "파일 등록에 실패했습니다.");
        }
        
        return resultMap;

    }
    
    /**
     *RPA 목록을 조회한다.
     */
    @RequestMapping("/cop/bbs/selectFileSap.do")
    public String selectFileSap(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
    	
    	if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
    	

    	Map<String, Object> map = bbsAttrbService.selectFileSap(boardMasterVO);
    	
    	
    	model.addAttribute("resultList", map.get("resultList"));

		return "cop/bbs/EgovBoardSapFileList";
    }
    
    /**
     *RPA 목록을 조회한다.
     */
    @RequestMapping("/cop/bbs/selectFileView.do")
    public String selectFileView(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
    	
    	if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
    	

    	Map<String, Object> map = bbsAttrbService.selectFileSap(boardMasterVO);
    	
    	
    	model.addAttribute("resultList", map.get("resultList"));

		return "cop/bbs/popupFileView";
    }
    
    /**
     *RPA 목록을 수정한다 kkk.
     */
    @RequestMapping(value="/cop/bbs/deleteFileSap.do", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> deleteFileSap(
            MultipartHttpServletRequest multiRequest,
            HttpServletRequest request,
            SessionVO sessionVO,
            ModelMap model,
            SessionStatus status) throws Exception {
	
    	String atchFileId = request.getParameter("atchFileId");
    	String blNo = request.getParameter("searchBlNo");
    	String fileGubun = request.getParameter("searchFileCn");
    	String filepath = request.getParameter("pathurl");    	
    	
    	BoardSapFileVO result = new BoardSapFileVO();
	    result.setAtchFileId(atchFileId);
	    result.setBlNo(blNo);
	    result.setFileGubun(fileGubun);
	    
	    
   	 // 요청에 응답하기 위한 맵 객체를 성한다.
        Map<String, Object> map = new HashMap<String, Object>();
	    
    	
        map = bbsAttrbService.deleteFileSap(result);
		
        
    	 // 요청에 응답하기 위한 맵 객체를 성한다.
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (map.get("process").toString().equals("0")) {
        	resultMap.put("filecnt", -1);
            resultMap.put("status", 1);
            resultMap.put("msg", "파일 삭제에 실패했습니다.");
        }
        else{
        	File file = new File(filepath); 
        	if( file.exists() ){ 
        		if(file.delete()){ 
        			log.info("파일삭제 성공"); 
        			}
        		else{ 
        			log.info("파일삭제 실패"); 
        			} 
        		}
        	else{ 
        		log.info("파일이 존재하지 않습니다."); 
        		} 

        		
            resultMap.put("filecnt", Integer.parseInt(map.get("FileCnt").toString()));
            resultMap.put("status", 0);
            resultMap.put("msg", "정상적으로 파일이 삭제되었습니다.");
        }
        
        return resultMap;
    }
    
    /**
     *RPA 목록을 조회한다.
     */
    @RequestMapping("/cop/bbs/pdfView.do")
    public String pdfView(@ModelAttribute("searchVO") BoardSapFileVO boardMaster, ModelMap model) throws Exception {
    	model.addAttribute("atchFileId", boardMaster.getAtchFileId());
		return "cop/bbs/pdfview";
    }
    

    /**
     *RPA 목록을 조회한다.
     */
    @RequestMapping("/cop/bbs/pdfFileView.do")
    public String pdfFileView(@ModelAttribute("searchVO") BoardMaster boardMaster, ModelMap model) throws Exception {
    	BoardSapFileVO vo = bbsAttrbService.selectFileSapDetail(boardMaster);
		
		String fileStreCours = vo.getFileStreCours();
		String orignlFileNm = vo.getOrignlFileNm();
		String streFileNm = vo.getStreFileNm();
		String pathurl = vo.getFileStreCours();
		pathurl = pathurl.replace("/homepage", "");
		
		model.addAttribute("pdfurl", pathurl+streFileNm);
    	//model.addAttribute("filename", boardMaster.getStreFileNm());
    	//model.addAttribute("realname", boardMaster.getOrignlFileNm());
		return "cop/bbs/pdffileview";
    }
    
    /**
     *RPA 목록을 조회한다.
     */
    @RequestMapping("/cop/bbs/fileView.do")
    public String fileView(@ModelAttribute("searchVO") BoardSapFileVO boardMaster, ModelMap model) throws Exception {
    	model.addAttribute("filepath", "/homepage/upload/sap/");
    	model.addAttribute("filename", boardMaster.getStreFileNm());
    	model.addAttribute("realname", boardMaster.getOrignlFileNm());
		return "cop/bbs/filedown";
    }
    
    /**
     *RPA 목록을 조회한다.
     */
    @RequestMapping("/cop/bbs/imgView.do")
    public String imgView(@ModelAttribute("searchVO") BoardSapFileVO boardMaster, ModelMap model) throws Exception {
    	model.addAttribute("atchFileId", boardMaster.getAtchFileId());
		return "cop/bbs/imgview";
    }
    
    @RequestMapping(value = "/display.do", method = RequestMethod.GET)
	public ResponseEntity<byte[]> displayFile(@ModelAttribute("searchVO") BoardMaster boardMaster, HttpServletRequest request)throws Exception{
		BoardSapFileVO vo = bbsAttrbService.selectFileSapDetail(boardMaster);
		
		String fileStreCours = vo.getFileStreCours();
		String orignlFileNm = vo.getOrignlFileNm();
		String streFileNm = vo.getStreFileNm();
		
		//fileStreCours = fileStreCours.replace("/homepage", "");
		//fileStreCours = "d:/" + fileStreCours;
	//	streFileNm = streFileNm.replace(".pdf","");
		
		InputStream in = null;
		ResponseEntity<byte[]> entity = null;
		//logger.info("FILE NAME : " + fileName);
		try {
			String formatName = streFileNm.substring(streFileNm.lastIndexOf(".")+1);
			MediaType mType = MediaUtils.getMediaType(formatName);
			HttpHeaders headers = new HttpHeaders();
			in = new FileInputStream(fileStreCours+streFileNm);
			
			//step: change HttpHeader ContentType
			if(mType != null) {
				//image file(show image)
				headers.setContentType(mType);
			}else {
				String client = request.getHeader("User-Agent");
				//fileName = fileName.substring(fileName.indexOf("_")+1);//original file Name
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				// IE
	            if(client.indexOf("MSIE") != -1){
	            	String realname = new String(orignlFileNm.getBytes("KSC5601"),"iso-8859-1");
	            	headers.add("Content-Disposition", "attachment; filename=\"" + realname+"\"");
	                
	            }else{
	                // 한글 파일명 처리
	            	String realname = new String(orignlFileNm.getBytes("utf-8"),"iso-8859-1");
	            	headers.add("Content-Disposition", "attachment; filename=\"" + realname+"\""); 
	            }
	            
			}
			
			entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED);
			
		}catch(Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
		}finally {
			in.close();
		}
			return entity;
		
	}
	
	
	@RequestMapping(value = "/pdfdown.do", method = RequestMethod.GET)
	public ResponseEntity<byte[]> pdfdown(@ModelAttribute("searchVO") BoardMaster boardMaster, HttpServletRequest request)throws Exception{
		BoardSapFileVO vo = bbsAttrbService.selectFileSapDetail(boardMaster);
		
		String fileStreCours = vo.getFileStreCours();
		String orignlFileNm = vo.getOrignlFileNm();
		String streFileNm = vo.getStreFileNm();
		
		InputStream in = null;
		ResponseEntity<byte[]> entity = null;
		//logger.info("FILE NAME : " + fileName);
		try {
			HttpHeaders headers = new HttpHeaders();
			in = new FileInputStream(fileStreCours+streFileNm);
			
		
			String client = request.getHeader("User-Agent");
			//fileName = fileName.substring(fileName.indexOf("_")+1);//original file Name
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			// IE
            if(client.indexOf("MSIE") != -1){
            	String realname = new String(orignlFileNm.getBytes("KSC5601"),"iso-8859-1");
            	headers.add("Content-Disposition", "attachment; filename=\"" + realname+"\"");
                
            }else{
                // 한글 파일명 처리
            	String realname = new String(orignlFileNm.getBytes("utf-8"),"iso-8859-1");
            	headers.add("Content-Disposition", "attachment; filename=\"" + realname+"\""); 
            }
			
			entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED);
			
		}catch(Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
		}finally {
			in.close();
		}
			return entity;
		
	}
	
	
	
	/**
     *RPA 목록을 수정한다 kkk.
     */
    @RequestMapping(value="/cop/bbs/insertForward.do", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> insertForward(
            MultipartHttpServletRequest multiRequest,
            HttpServletRequest request,
            SessionVO sessionVO,
            ModelMap model,
            SessionStatus status) throws Exception {
    	
    	String t3_inno = request.getParameter("t3_inno");
    	String t3_blno = request.getParameter("t3_blno");
    	String t3_sjdt = request.getParameter("t3_sjdt");
    	
    	BoardMasterVO result = new BoardMasterVO();
	    result.sett3_inno(t3_inno);
	    result.sett3_blno(t3_blno);
	    result.sett3_sjdt(t3_sjdt);
	    result.sett3_regflag("Y");
	    
	    
   	 	int process = bbsAttrbService.insertForward(result);
		
        
    	 // 요청에 응답하기 위한 맵 객체를 성한다.
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (process == -1) {
            resultMap.put("status", -1);
            resultMap.put("msg", "중복데이타가 존재합니다.");
        }
        else if (process == 0) {
            resultMap.put("status", 1);
            resultMap.put("msg", "데이타 등록에 실패했습니다.");
        }
        else{
        	resultMap.put("status", 0);
            resultMap.put("msg", "정상적으로 등록했습니다.");
        }
        
        return resultMap;
    }
    
    
    /**
     *RPA 목록을 수정한다 kkk.
     */
    @RequestMapping("/cop/bbs/deleteForward.do")
    //public String updateBBSMasterInf(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO,  ModelMap model) throws Exception {
    public String deleteForward(
    		//@RequestParam("confirmYn") String confirmYn ,
    		//@RequestParam("searchBLNo") String searchBLNo ,
    		@ModelAttribute("searchVO") BoardMasterVO boardMasterVO,
    	    BindingResult bindingResult, ModelMap model) throws Exception {
	
    	if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
    	
		bbsAttrbService.deleteForward(boardMasterVO);
		
		return "forward:/cop/bbs/SelectBBSMasterInfs.do";
    }
    
    
    /**
     *RPA 목록을 수정한다 kkk.
     */
    @SuppressWarnings("deprecation")
	@RequestMapping(value="/cop/bbs/confirmIncome.do", method = {RequestMethod.GET, RequestMethod.POST})
    public String confirmIncome(
    		@ModelAttribute("searchVO") BoardMasterVO boardMasterVO,
    	    BindingResult bindingResult, ModelMap model) throws Exception {
	
    	if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM", Locale.KOREA);
    	SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.MONTH , -1);

    	String checkMonth = boardMasterVO.getSearchMonth();
    	
    	String firstDate = "";
    	String lastDate = "";
    	 
    	if ((boardMasterVO.getSearchMonth() == null) || (boardMasterVO.getSearchMonth().equals(""))) {
    		cal.set(Calendar.DATE, 1);
    		 
    		checkMonth = formatter.format(cal.getTime());
    	    
    		boardMasterVO.setSearchMonth(checkMonth);
    		
        	cal.set(Calendar.DATE, 1);
            firstDate = formatter1.format(cal.getTime());
        	boardMasterVO.setSearchBgnDe(firstDate);
        	
        	cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        	lastDate = formatter1.format(cal.getTime());
    		boardMasterVO.setSearchEndDe(lastDate);
    	}
    	else {
    		Date tempDate = formatter1.parse(checkMonth+"01");
    		Date date = new SimpleDateFormat("yyyyMMdd").parse(checkMonth+"01");
    		String newstring = new SimpleDateFormat("yyyyMMdd").format(date);
    		cal.set(Calendar.YEAR, Integer.parseInt(newstring.substring(0, 4)));
    		cal.set(Calendar.MONTH, Integer.parseInt(newstring.substring(4, 6)) - 1);
    		cal.set(Calendar.DATE, Integer.parseInt(newstring.substring(6, 8)));


//    		cal.set(tempDate.getYear(), tempDate.getMonth(), tempDate.getDay());
    		int ny = cal.get(Calendar.YEAR);
    		int nm = cal.get(Calendar.MONTH) + 1;
    		int nd = cal.get(Calendar.DATE);
    		
    		cal.set(Calendar.DATE, 1);
            firstDate = formatter1.format(cal.getTime());
        	boardMasterVO.setSearchBgnDe(firstDate);
        	
        	cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        	lastDate = formatter1.format(cal.getTime());
    		boardMasterVO.setSearchEndDe(lastDate);
    	}
    	
        bbsAttrbService.insertRpaIncome(boardMasterVO);
		
        
        return "forward:/cop/bbs/selectExtraSPList.do";
    }
    
    /**
     *RPA 목록을 조회한다.
     */
    @RequestMapping("/cop/bbs/popupExtraExcel.do")
    public String popupExtraExcel(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
    	
    	if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
    	
		return "cop/bbs/popupExtraExcel";
    }
    
    @RequestMapping(value = "/file/fileUploadExtra.do")
    @ResponseBody
    public Map<String, Object> fileUploadExtra(
                         MultipartHttpServletRequest multiRequest,
                         HttpServletRequest request,
                         SessionVO sessionVO,
                         ModelMap model,
                         SessionStatus status) throws Exception{
              
    	//파일이 생성되고나면 생성된 첨부파일 ID를 리턴한다.
    	int process = bbsAttrbService.insertRpaExcel(multiRequest,request);  
        
     // 요청에 응답하기 위한 맵 객체를 성한다.
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (process > -1) {
            resultMap.put("status", 0);
            resultMap.put("msg", "정상적으로 파일이 등록되었습니다.");
        }
        else{
            resultMap.put("status", 1);
            resultMap.put("msg", "파일 등록에 실패했습니다.");
        }
        
        return resultMap;

    }
    
    
    /**
     *SAP 다운을 위한  목록을 조회한다.
     */
    @RequestMapping("cop/bbs/SelectBBSTTSendList.do")
    public String SelectBBSTTSendList(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, ModelMap model) throws Exception {
    	
    	if (!checkAuthority(model)) return "cmm/uat/uia/EgovLoginUsr";	// server-side 권한 확인
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    	Calendar cal = Calendar.getInstance();
    	
    	String firstDate = boardMasterVO.getSearchBgnDe();
    	String lastDate = boardMasterVO.getSearchEndDe();
    	String send_dt = boardMasterVO.getSearchSendDt();    	
    	
    	String searchDate = boardMasterVO.getSearchBgnDe();
    	
    	if ((boardMasterVO.getSearchGubun() == null) || (boardMasterVO.getSearchGubun().equals(""))) {
        	boardMasterVO.setSearchGubun("BT");
    	}
    	if ((boardMasterVO.getSearchBgnDe() == null) || (boardMasterVO.getSearchBgnDe().equals(""))) {
        	cal.set(Calendar.DATE, 1);
       	 
            firstDate = formatter.format(cal.getTime());
            
        	boardMasterVO.setSearchBgnDe(firstDate);
    	}
    	if ((boardMasterVO.getSearchEndDe() == null) || (boardMasterVO.getSearchEndDe().equals(""))) {
        	cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        	
        	lastDate = formatter.format(cal.getTime());
    		boardMasterVO.setSearchEndDe(lastDate);
    	}
    	
    	if ((boardMasterVO.getSearchSendDt() == null) || (boardMasterVO.getSearchSendDt().equals(""))) {
        	
    		cal.set(Calendar.DATE, 1);
          	 
    		send_dt = formatter.format(cal.getTime());
            
        	boardMasterVO.setSearchSendDt(send_dt);
    	}
    	 
    	if ((searchDate != null) && !(searchDate.equals(""))) {
        	Map<String, Object> map = bbsAttrbService.selectTTSendList(boardMasterVO);
        	
        	List<BoardMasterVO> data =  (List<BoardMasterVO>) map.get("resultList");
        	
        	//GROUP BY된 데이터를 받을 MAP
        	Map<String, Map<String, Object>> resultMap = new HashMap<String, Map<String, Object>>();

        	for(int i=0; i<data.size(); i++){
        		String orderNumber = data.get(i).getwaers().toString(); //KEY VALUE
        		if(resultMap.containsKey(orderNumber)){
        			//KEY값이 존재하면 해당 키값의 해당되는 가격을 가져와 더해줌
        			resultMap.get(orderNumber).put("wrbtr", Double.parseDouble(resultMap.get(orderNumber).get("wrbtr").toString()) 
        			+ Double.parseDouble(data.get(i).getwrbtr().toString()));
        		}else{
        			//KEY값이 존재하지 않으면 MAP에 데이터를 넣어줌
        			Map<String, Object> dataMap = new HashMap<String, Object>();
        			dataMap.put("wrbtr", Double.parseDouble(data.get(i).getwrbtr().toString()));
        			resultMap.put(orderNumber, dataMap);
        		}
        	}

        	
        	
    		model.addAttribute("resultList", map.get("resultList"));
    		model.addAttribute("totalList", resultMap);
    	}
    	


		return "cop/bbs/EgovBoardTTSendList";
    }
    
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody    
    @RequestMapping("/cop/bbs/SelectBBSTTSendExcel.do")
    public Map<Object, Object> SelectBBSTTSendExcel(@ModelAttribute("searchVO") BoardMasterVO boardMasterVO, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	Map<Object, Object> returnMap = new HashMap<Object, Object>();
    	
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
			String send_dt = boardMasterVO.getSearchSendDt();  
			String searchCom = boardMasterVO.getSearchCom();  
			
			Date tempDate = formatter.parse(send_dt);
			
			String send_date = formatter1.format(tempDate);
			
			String excelTitle = "TT 송금 내역서";
			
			Map<String, Object> map = bbsAttrbService.selectTTSendList(boardMasterVO);
        	
        	List<BoardMasterVO> lstResult =  (List<BoardMasterVO>) map.get("resultList");
        	
        	//GROUP BY된 데이터를 받을 MAP
        	Map<String, Map<String, Object>> resultMap = new HashMap<String, Map<String, Object>>();

        	for(int i=0; i<lstResult.size(); i++){
        		String orderNumber = lstResult.get(i).getwaers().toString(); //KEY VALUE
        		if(resultMap.containsKey(orderNumber)){
        			//KEY값이 존재하면 해당 키값의 해당되는 가격을 가져와 더해줌
        			resultMap.get(orderNumber).put("wrbtr", Double.parseDouble(resultMap.get(orderNumber).get("wrbtr").toString()) 
        			+ Double.parseDouble(lstResult.get(i).getwrbtr().toString()));
        		}else{
        			//KEY값이 존재하지 않으면 MAP에 데이터를 넣어줌
        			Map<String, Object> dataMap = new HashMap<String, Object>();
        			dataMap.put("wrbtr", Double.parseDouble(lstResult.get(i).getwrbtr().toString()));
        			resultMap.put(orderNumber, dataMap);
        		}
        	}
        	
        	Set set = resultMap.entrySet();
        	Iterator iterator = set.iterator();

        	while(iterator.hasNext()){
        	  Map.Entry entry = (Map.Entry)iterator.next();
        	  String key = (String)entry.getKey();
        	  Map<String, Object> valueMap = (Map<String, Object>) entry.getValue();
        	  
       	  
        	  BoardMasterVO vo = new BoardMasterVO();
        	  vo.setNum("");
        	  vo.setbktxt(key + " 합계");
              vo.setwaers(key);
              vo.setwrbtr(valueMap.get("wrbtr").toString());
              lstResult.add(vo);

        	}
        	
			Map<String, Object> tempMap = new HashMap<String, Object>();
    	
			//첫Row,마지막Row,첫cell,마지막cell, row높이, 스타일, 내용
			Map<String, Object> headerMap = new HashMap<String, Object>();
			
			headerMap.put("sRow", "1");
			headerMap.put("eRow", "1");
			headerMap.put("sCol", "0");
			headerMap.put("eCol", "6");
			headerMap.put("fontType", "titleLine");
			headerMap.put("fontColor", "000000");
			headerMap.put("styleColor", "FFFFFF");
			headerMap.put("textAlign", "center");
			headerMap.put("textVAlign", "center");
			headerMap.put("line", "none");
			headerMap.put("title", excelTitle);
			
			//첫Row,마지막Row,첫cell,마지막cell, row높이, 스타일, 내용
			Map<String, Object> unitMap = new HashMap<String, Object>();
			
			unitMap.put("sRow", "2");
			unitMap.put("eRow", "3");
			unitMap.put("sCol", "0");
			unitMap.put("eCol", "6");
			unitMap.put("fontType", "unitLine");
			unitMap.put("fontColor", "000000");
			unitMap.put("styleColor", "FFFFFF");
			unitMap.put("textAlign", "left");
			unitMap.put("textVAlign", "center");
			unitMap.put("line", "none");
			unitMap.put("title", "");
			
			List<Map<String, Object>> unitList = new ArrayList<Map<String, Object>>();
			
			tempMap = new HashMap<String, Object>();
			tempMap.put("sRow", "2");
			tempMap.put("eRow", "2");
			tempMap.put("sCol", "6");
			tempMap.put("eCol", "6");
			tempMap.put("fontType", "unitLine");
			tempMap.put("fontColor", "000000");
			tempMap.put("styleColor", "FFFFFF");
			tempMap.put("textAlign", "left");
			tempMap.put("textVAlign", "center");
			tempMap.put("line", "none");
			tempMap.put("title", "송금처 : " + searchCom);
			unitList.add(tempMap);
			
			tempMap = new HashMap<String, Object>();
			tempMap.put("sRow", "3");
			tempMap.put("eRow", "3");
			tempMap.put("sCol", "6");
			tempMap.put("eCol", "6");
			tempMap.put("fontType", "unitLine");
			tempMap.put("fontColor", "000000");
			tempMap.put("styleColor", "FFFFFF");
			tempMap.put("textAlign", "left");
			tempMap.put("textVAlign", "center");
			tempMap.put("line", "none");
			tempMap.put("title", "송금일 : " + send_date);
			unitList.add(tempMap);
			
			
			Map<String, Object> titleStyleMap = new HashMap<String, Object>();
			
			titleStyleMap.put("sRow", "4");
			titleStyleMap.put("eRow", "4");
			titleStyleMap.put("sCol", "0");
			titleStyleMap.put("eCol", "6");
			titleStyleMap.put("fontType", "subtitle");
			titleStyleMap.put("fontColor", "000000");
			titleStyleMap.put("styleColor", "ECF5FC");
			titleStyleMap.put("textAlign", "center");
			titleStyleMap.put("textVAlign", "center");
			titleStyleMap.put("line", "line");
			
			List<Map<String, Object>> fieldInfoList = new ArrayList<Map<String, Object>>();
			
			tempMap = new HashMap<String, Object>();
			tempMap.put("field", "num");
			tempMap.put("cellTitle", "NO");
			tempMap.put("fileType", "String");
			tempMap.put("cellWidth", 7*256);
			tempMap.put("fontType", "content");
			tempMap.put("fontColor", "000000");
			tempMap.put("styleColor", "FFFFFF");
			tempMap.put("textAlign", "center");
			tempMap.put("textVAlign", "center");
			tempMap.put("line", "dot");
			tempMap.put("fomule", "");
			fieldInfoList.add(tempMap);
			
			tempMap = new HashMap<String, Object>();
			tempMap.put("field", "bldat");
			tempMap.put("cellTitle", "BL일자");
			tempMap.put("fileType", "String");
			tempMap.put("cellWidth", 13*256);
			tempMap.put("fontType", "content");
			tempMap.put("fontColor", "000000");
			tempMap.put("styleColor", "FFFFFF");
			tempMap.put("textAlign", "center");
			tempMap.put("textVAlign", "center");
			tempMap.put("line", "dot");
			tempMap.put("fomule", "");
			fieldInfoList.add(tempMap);
			
			tempMap = new HashMap<String, Object>();
			tempMap.put("field", "bktxt");
			tempMap.put("cellTitle", "BL 번호");
			tempMap.put("fileType", "String");
			tempMap.put("cellWidth", 13*256);
			tempMap.put("fontType", "content");
			tempMap.put("fontColor", "000000");
			tempMap.put("styleColor", "FFFFFF");
			tempMap.put("textAlign", "center");
			tempMap.put("textVAlign", "center");
			tempMap.put("line", "dot");
			tempMap.put("fomule", "");
			fieldInfoList.add(tempMap);
			
			tempMap = new HashMap<String, Object>();
			tempMap.put("field", "zfidrno");
			tempMap.put("cellTitle", "수입신고번호");
			tempMap.put("fileType", "String");
			tempMap.put("cellWidth", 15*256);
			tempMap.put("fontType", "content");
			tempMap.put("fontColor", "000000");
			tempMap.put("styleColor", "FFFFFF");
			tempMap.put("textAlign", "center");
			tempMap.put("textVAlign", "center");
			tempMap.put("line", "dot");
			tempMap.put("fomule", "");
			fieldInfoList.add(tempMap);
			
			/*tempMap = new HashMap<String, Object>();
			tempMap.put("field", "send_dt");
			tempMap.put("cellTitle", "송금일");
			tempMap.put("fileType", "String");
			tempMap.put("cellWidth", 13*256);
			tempMap.put("fontType", "content");
			tempMap.put("fontColor", "000000");
			tempMap.put("styleColor", "FFFFFF");
			tempMap.put("textAlign", "center");
			tempMap.put("textVAlign", "center");
			tempMap.put("line", "dot");
			tempMap.put("fomule", "");
			fieldInfoList.add(tempMap);
			*/
			
			tempMap = new HashMap<String, Object>();
			tempMap.put("field", "waers");
			tempMap.put("cellTitle", "통화단위");
			tempMap.put("fileType", "String");
			tempMap.put("cellWidth", 13*256);
			tempMap.put("fontType", "content");
			tempMap.put("fontColor", "000000");
			tempMap.put("styleColor", "FFFFFF");
			tempMap.put("textAlign", "center");
			tempMap.put("textVAlign", "center");
			tempMap.put("line", "dot");
			tempMap.put("fomule", "");
			fieldInfoList.add(tempMap);
			
			tempMap = new HashMap<String, Object>();
			tempMap.put("field", "wrbtr");
			tempMap.put("cellTitle", "결재금액/외화금액");
			tempMap.put("fileType", "Int");
			tempMap.put("cellWidth", 18*256);
			tempMap.put("fontType", "content");
			tempMap.put("fontColor", "000000");
			tempMap.put("styleColor", "FFFFFF");
			tempMap.put("textAlign", "right");
			tempMap.put("textVAlign", "center");
			tempMap.put("line", "dot");
			tempMap.put("fomule", "* #,##0_-;-* #,##0_-;_-* \"-\"_-;_-@_-");
			fieldInfoList.add(tempMap);			

			/*tempMap = new HashMap<String, Object>();
			tempMap.put("field", "name1");
			tempMap.put("cellTitle", "송금처");
			tempMap.put("fileType", "String");
			tempMap.put("cellWidth", 25*256);
			tempMap.put("fontType", "content");
			tempMap.put("fontColor", "000000");
			tempMap.put("styleColor", "FFFFFF");
			tempMap.put("textAlign", "left");
			tempMap.put("textVAlign", "center");
			tempMap.put("line", "dot");
			tempMap.put("fomule", "");
			fieldInfoList.add(tempMap);
			*/

			tempMap = new HashMap<String, Object>();
			tempMap.put("field", "kursf");
			tempMap.put("cellTitle", "비고(환율)");
			tempMap.put("fileType", "String");
			tempMap.put("cellWidth", 25*256);
			tempMap.put("fontType", "content");
			tempMap.put("fontColor", "000000");
			tempMap.put("styleColor", "FFFFFF");
			tempMap.put("textAlign", "left");
			tempMap.put("textVAlign", "center");
			tempMap.put("line", "dot");
			tempMap.put("fomule", "");
			fieldInfoList.add(tempMap);
			
			Map<String, Object> mergeCellMap = new HashMap<String, Object>();
			
			mergeCellMap.put("zfapnm", "1");
			mergeCellMap.put("name1", "2");
					
			Map<String, Object> excelInfpMap = new HashMap<String, Object>();
			excelInfpMap.put("headerMap", headerMap);		
			excelInfpMap.put("unitMap", unitMap);
			excelInfpMap.put("titleStyleMap", titleStyleMap);
			excelInfpMap.put("mergeCellMap", mergeCellMap);
			excelInfpMap.put("unitList", unitList);
			excelInfpMap.put("fieldInfoList", fieldInfoList);
			
			
			SXSSFWorkbook workbook = bbsAttrbService.buildExcelXSS(excelTitle,excelInfpMap, lstResult, false);

			String realExcelFilename = null;
			realExcelFilename = URLEncoder.encode(excelTitle, "UTF-8");
			realExcelFilename = realExcelFilename.replaceAll("\\+", " ");

			/*
			 * HTTP Header 설정.
			 */
			response.setContentType("application/vnd.ms-excel; name=\"" + realExcelFilename + ".xlsx\"");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + realExcelFilename + ".xlsx\"");
			response.setHeader("Content-Transfer-Encoding", "binary");
//            response.setHeader("Content-Length", Long.toString(fileDownLoadInputVO.getlFileSize()));
			response.setHeader("Cache-Control", "no-cahe, no-store, must-revalidate\r\n");
			response.setHeader("Connection", "close");

			//FileOutputStream fileOut = new FileOutputStream(realExcelFilename + ".xlsx");
			//OutputStream out = response.getOutputStream();
			OutputStream fileOut = response.getOutputStream();

			workbook.write(fileOut);
			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}


		return returnMap;
    }
        

}

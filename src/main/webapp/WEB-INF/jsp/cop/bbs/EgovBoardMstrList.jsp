<%--
  Class Name : EgovFileList.jsp
  Description : 파일목록화면
  Modification Information
 
      수정일         수정자                   수정내용
    -------    --------    ---------------------------
     2009.03.12   이삼섭          최초 생성
     2011.08.31  JJY       경량환경 버전 생성
 
    author   : 공통서비스 개발팀 이삼섭
    since    : 2009.03.12
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
	<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui"%>
 --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" >
<meta http-equiv="X-UA-Compatible" content="IE=edage,chrome=1" >
<title>게시판 목록</title>
<link href="<c:url value='/'/>css/common.css" rel="stylesheet" type="text/css" >

<script type="text/javascript" src="<c:url value='/js/EgovBBSMng.js' />"></script>
<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.js" ></script>

<script src="http://code.jquery.com/jquery-1.10.2.js"></script>
<script src="http://code.jquery.com/ui/1.11.2/jquery-ui.js"></script>
<script src="https://malsup.github.io/jquery.form.js"></script>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<style>
/*datepicer 버튼 롤오버 시 손가락 모양 표시*/
.ui-datepicker-trigger{cursor: pointer;}
/*datepicer input 롤오버 시 손가락 모양 표시*/
.hasDatepicker{cursor: pointer;}

</style>


<script type="text/javascript">
	 window.history.forward();
	 function noBack(){window.history.forward();}
</script>

<script type="text/javascript">
	//다건입력 여부
	var tmpChk = "0";
		
	
	function fn_egov_select_brdMstr2(){
		
			document.frm.action = "<c:url value='/cop/bbs/SelectBBSMasterInfs.do'/>";
			document.frm.submit();
			
	}
	
	
	function fn_egov_select_brdMstr(num){
		
		if(num != 1){
			return;
			
		}else{
			document.frm.action = "<c:url value='/cop/bbs/SelectBBSMasterInfs.do'/>";
			document.frm.submit();
			
		}
	}
	
	function fn_egov_forward(){
		var url = "/cop/bbs/popupForward.do" ;
		var param = {'searchBgnDe':$("#searchBgnDe").val(),'searchEndDe':$("#searchEndDe").val(),'searchBl':$("#searchBl").val(),'searchCnd' :$("#searchCnd").val()};
		var popName = "forwardReg";
		setPostPopupWin(url,param,popName,0,0,550,500,'auto');
	}
	
	
	function fn_egov_excel_download(num){
		
		if($('#searchBlNo').val() ==""){
			alert("확정 여부를 처리할 대상이 없습니다!");
			return;
		}
		
		
		if(num == 'Y'){
			if ($('#regFlag').val() == "R"){
				var result = confirm('확정 진행을 하시겠습니까?'); 
				if(result) { 
					$('#confirmYn').val("Y");
					document.frm.action = "<c:url value='/cop/bbs/UpdateBBSMasterInf.do'/>";
				} else { 
					return fase;
				} 
			}
			else if($('#regFlag').val() == "Y"){
				var result = confirm('수기확정 진행을 하시겠습니까?'); 
				if(result) { 
					$('#confirmYn').val("S");
					document.frm.action = "<c:url value='/cop/bbs/UpdateBBSMasterInf.do'/>";
				} else { 
					return fase; 
				} 
			}
			else{
				document.frm.action = "<c:url value='/cop/bbs/SelectBBSMasterInfsPop.do'/>";
				
			}
		}
		else if(num == 'N'){
			if(($('#regFlag').val() == "C") || ($('#regFlag').val() == "P")){
				var result = confirm('확정 취소를 하시겠습니까?'); 
				if(result) { 
					$('#confirmYn').val("N");
					document.frm.action = "<c:url value='/cop/bbs/UpdateBBSMasterInf.do'/>";
				} else { 
					return fase; 
				} 
			}
			else if($('#regFlag').val() == "F"){
				alert("확인건은 확정 취소를 할 수 없습니다!");
				return;
			}
			else if($('#regFlag').val() == "L"){
				alert("승인건은 확정 취소를 할 수 없습니다!");
				return;
			}
			else if(($('#regFlag').val() == "R") ||($('#regFlag').val() == "Y")){
				alert("확정되지 않은 건에 대한 확정 취소를 할 수 없습니다.");
				return;
			}
				
		}
		
		//old
		//document.frm.action = "<c:url value='/cop/bbs/SelectBBSMasterInfsPop.do'/>";
		
		
		
		document.frm.submit();
		
	}
	
	//날짜입력 체크
	$(document).ready(function() {
		
		$("#searchBgnDe").keyup(function(){$(this).val( $(this).val().replace(/[^0-9]/g,"") );} );
		$("#searchEndDe").keyup(function(){$(this).val( $(this).val().replace(/[^0-9]/g,"") );} );

		$("#searchWrd").keyup(function(){$(this).val( $(this).val().replace(/[^\!-z]/g,"") );} );
		

	});

	
   $(function(){
	   $("#searchBgnDe").datepicker({
           dateFormat: 'yymmdd' //Input Display Format 변경
           ,showOtherMonths: true //빈 공간에 현재월의 앞뒤월의 날짜를 표시
           ,showMonthAfterYear:true //년도 먼저 나오고, 뒤에 월 표시
           ,changeYear: true //콤보박스에서 년 선택 가능
           ,changeMonth: true //콤보박스에서 월 선택 가능                
           ,showOn: "both" //button:버튼을 표시하고,버튼을 눌러야만 달력 표시 ^ both:버튼을 표시하고,버튼을 누르거나 input을 클릭하면 달력 표시  
           ,buttonImage: "http://jqueryui.com/resources/demos/datepicker/images/calendar.gif" //버튼 이미지 경로
           ,buttonImageOnly: true //기본 버튼의 회색 부분을 없애고, 이미지만 보이게 함
           ,buttonText: "선택" //버튼에 마우스 갖다 댔을 때 표시되는 텍스트                
           ,yearSuffix: "년" //달력의 년도 부분 뒤에 붙는 텍스트
           ,monthNamesShort: ['1','2','3','4','5','6','7','8','9','10','11','12'] //달력의 월 부분 텍스트
           ,monthNames: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'] //달력의 월 부분 Tooltip 텍스트
           ,dayNamesMin: ['일','월','화','수','목','금','토'] //달력의 요일 부분 텍스트
           ,dayNames: ['일요일','월요일','화요일','수요일','목요일','금요일','토요일'] //달력의 요일 부분 Tooltip 텍스트
           //,minDate: "-1M" //최소 선택일자(-1D:하루전, -1M:한달전, -1Y:일년전)
           ,maxDate: "+1M" //최대 선택일자(+1D:하루후, -1M:한달후, -1Y:일년후)                
       });  
	   $("#searchEndDe").datepicker({
           dateFormat: 'yymmdd' //Input Display Format 변경
           ,showOtherMonths: true //빈 공간에 현재월의 앞뒤월의 날짜를 표시
           ,showMonthAfterYear:true //년도 먼저 나오고, 뒤에 월 표시
           ,changeYear: true //콤보박스에서 년 선택 가능
           ,changeMonth: true //콤보박스에서 월 선택 가능                
           ,showOn: "both" //button:버튼을 표시하고,버튼을 눌러야만 달력 표시 ^ both:버튼을 표시하고,버튼을 누르거나 input을 클릭하면 달력 표시  
           ,buttonImage: "http://jqueryui.com/resources/demos/datepicker/images/calendar.gif" //버튼 이미지 경로
           ,buttonImageOnly: true //기본 버튼의 회색 부분을 없애고, 이미지만 보이게 함
           ,buttonText: "선택" //버튼에 마우스 갖다 댔을 때 표시되는 텍스트                
           ,yearSuffix: "년" //달력의 년도 부분 뒤에 붙는 텍스트
           ,monthNamesShort: ['1','2','3','4','5','6','7','8','9','10','11','12'] //달력의 월 부분 텍스트
           ,monthNames: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'] //달력의 월 부분 Tooltip 텍스트
           ,dayNamesMin: ['일','월','화','수','목','금','토'] //달력의 요일 부분 텍스트
           ,dayNames: ['일요일','월요일','화요일','수요일','목요일','금요일','토요일'] //달력의 요일 부분 Tooltip 텍스트
           //,minDate: "-1M" //최소 선택일자(-1D:하루전, -1M:한달전, -1Y:일년전)
           ,maxDate: "+1M" //최대 선택일자(+1D:하루후, -1M:한달후, -1Y:일년후)                
       });  
	   
		$("input:radio[name=checkBtn]").click(function()
	    {
			if ($("input:radio[name='checkBtn']").is(":checked") == true){
				var radioValue = $('input:radio[name="checkBtn"]:checked').val();
				var blno = $("#blno"+radioValue).text();
				var t1_yn = $("#t1_yn"+radioValue).text();
				var t2_yn = $("#t2_yn"+radioValue).text();
				var t3_yn = $("#t3_yn"+radioValue).text();
				var t4_yn = $("#t4_yn"+radioValue).text();
				$('#searchBlNo').val(blno);
				
				if(t1_yn == "등록" && t2_yn == "등록" && t3_yn == "등록" && t4_yn == "등록"   ){
				  	 $('#regFlag').val('R');	  
				  	
				  }else	if(t1_yn == "확정" && t2_yn == "확정" && t3_yn == "확정" && t4_yn == "확정"   ){
					  	 $('#regFlag').val('C');	  
					 
				  }else	if(t1_yn == "수기" && t2_yn == "수기" && t3_yn == "수기" && t4_yn == "수기"   ){
					  	 $('#regFlag').val('P');	  
				  }else	if(t1_yn == "임시" && t2_yn == "임시" && t3_yn == "임시" && t4_yn == "임시"   ){
					  	 $('#regFlag').val('T');	  
					 
				  }else	if(t1_yn == "확인" && t2_yn == "확인" && t3_yn == "확인" && t4_yn == "확인"   ){
					  	 $('#regFlag').val('F');	  
				  }else	if(t1_yn == "승인" && t2_yn == "승인" && t3_yn == "승인" && t4_yn == "승인"   ){
					  	 $('#regFlag').val('L');	  
					 
				  }else{
					  $('#regFlag').val('Y');
					  
				  }
			}
	    });
	    
		$("a[name='c1yn']").click(function(){
			var url = "/cop/bbs/selectFileSap.do" ;
			var confirmValue = $(this).attr("result_id");
			var searchCompany = "Y";
			if (confirmValue == "L") searchCompany = "N";
			var param = {'searchBlNo':$(this).attr("content_id"),'searchFileCn':'FR00','searchCompany':searchCompany,'searchBgnDe':$("#searchBgnDe").val(),'searchEndDe':$("#searchEndDe").val(),'searchBl':$("#searchBl").val(),'searchCnd' :$("#searchCnd").val()};
			var popName = "fileView";
			setPostPopupWin(url,param,popName,0,0,870,800,'auto');
        });
		
		$("a[name='c2yn']").click(function(){
			var url = "/cop/bbs/selectFileSap.do" ;
			var confirmValue = $(this).attr("result_id");
			var searchCompany = "Y";
			if (confirmValue == "L") searchCompany = "N";
			var param = {'searchBlNo':$(this).attr("content_id"),'searchFileCn':'FR06','searchCompany':searchCompany,'searchBgnDe':$("#searchBgnDe").val(),'searchEndDe':$("#searchEndDe").val(),'searchBl':$("#searchBl").val(),'searchCnd' :$("#searchCnd").val()};
			var popName = "fileView";
			setPostPopupWin(url,param,popName,0,0,870,800,'auto');
        });
		
		$("a[name='c3yn']").click(function(){
			var url = "/cop/bbs/selectFileSap.do" ;
			var confirmValue = $(this).attr("result_id");
			var searchCompany = "Y";
			if (confirmValue == "L") searchCompany = "N";
			var param = {'searchBlNo':$(this).attr("content_id"),'searchFileCn':'FR02','searchCompany':searchCompany,'searchBgnDe':$("#searchBgnDe").val(),'searchEndDe':$("#searchEndDe").val(),'searchBl':$("#searchBl").val(),'searchCnd' :$("#searchCnd").val()};
			var popName = "fileView";
			setPostPopupWin(url,param,popName,0,0,870,800,'auto');
        });
		
		$("a[name='c4yn']").click(function(){
			var url = "/cop/bbs/selectFileSap.do" ;
			var confirmValue = $(this).attr("result_id");
			var searchCompany = "Y";
			if (confirmValue == "L") searchCompany = "N";
			var param = {'searchBlNo':$(this).attr("content_id"),'searchFileCn':'FR04','searchCompany':searchCompany,'searchBgnDe':$("#searchBgnDe").val(),'searchEndDe':$("#searchEndDe").val(),'searchBl':$("#searchBl").val(),'searchCnd' :$("#searchCnd").val()};
			var popName = "fileView";
			setPostPopupWin(url,param,popName,0,0,870,800,'auto');
        });
		
		$("a[name='c5yn']").click(function(){
			var url = "/cop/bbs/selectFileSap.do" ;
			var confirmValue = $(this).attr("result_id");
			var searchCompany = "Y";
			if (confirmValue == "L") searchCompany = "N";
			var param = {'searchBlNo':$(this).attr("content_id"),'searchFileCn':'FR01','searchCompany':searchCompany,'searchBgnDe':$("#searchBgnDe").val(),'searchEndDe':$("#searchEndDe").val(),'searchBl':$("#searchBl").val(),'searchCnd' :$("#searchCnd").val()};
			var popName = "fileView";
			setPostPopupWin(url,param,popName,0,0,870,800,'auto');
        });
		
		$("a[name='c6yn']").click(function(){
			var url = "/cop/bbs/selectFileSap.do" ;
			var confirmValue = $(this).attr("result_id");
			var searchCompany = "Y";
			if (confirmValue == "L") searchCompany = "N";
			var param = {'searchBlNo':$(this).attr("content_id"),'searchFileCn':'FR03','searchCompany':searchCompany,'searchBgnDe':$("#searchBgnDe").val(),'searchEndDe':$("#searchEndDe").val(),'searchBl':$("#searchBl").val(),'searchCnd' :$("#searchCnd").val()};
			var popName = "fileView";
			setPostPopupWin(url,param,popName,0,0,870,800,'auto');
        });
		
		$("a[name='c7yn']").click(function(){
			var url = "/cop/bbs/selectFileSap.do" ;
			var confirmValue = $(this).attr("result_id");
			var searchCompany = "Y";
			if (confirmValue == "L") searchCompany = "N";
			var param = {'searchBlNo':$(this).attr("content_id"),'searchFileCn':'FR05','searchCompany':searchCompany,'searchBgnDe':$("#searchBgnDe").val(),'searchEndDe':$("#searchEndDe").val(),'searchBl':$("#searchBl").val(),'searchCnd' :$("#searchCnd").val()};
			var popName = "fileView";
			setPostPopupWin(url,param,popName,0,0,870,800,'auto');
        });
		
		$("a[name='c8yn']").click(function(){
			var url = "/cop/bbs/selectFileSap.do" ;
			var confirmValue = $(this).attr("result_id");
			var searchCompany = "Y";
			if (confirmValue == "L") searchCompany = "N";
			var param = {'searchBlNo':$(this).attr("content_id"),'searchFileCn':'FR08','searchCompany':searchCompany,'searchBgnDe':$("#searchBgnDe").val(),'searchEndDe':$("#searchEndDe").val(),'searchBl':$("#searchBl").val(),'searchCnd' :$("#searchCnd").val()};
			var popName = "fileView";
			setPostPopupWin(url,param,popName,0,0,870,800,'auto');
        });

		$("a[name='c9yn']").click(function(){
			var url = "/cop/bbs/selectFileSap.do" ;
			var confirmValue = $(this).attr("result_id");
			var searchCompany = "Y";
			if (confirmValue == "L") searchCompany = "N";
			var param = {'searchBlNo':$(this).attr("content_id"),'searchFileCn':'FR07','searchCompany':searchCompany,'searchBgnDe':$("#searchBgnDe").val(),'searchEndDe':$("#searchEndDe").val(),'searchBl':$("#searchBl").val(),'searchCnd' :$("#searchCnd").val()};
			var popName = "fileView";
			setPostPopupWin(url,param,popName,0,0,870,800,'auto');
        });
		
		$("a[name='c10yn']").click(function(){
			var url = "/cop/bbs/selectFileSap.do" ;
			var confirmValue = $(this).attr("result_id");
			var searchCompany = "Y";
			if (confirmValue == "L") searchCompany = "N";
			var param = {'searchBlNo':$(this).attr("content_id"),'searchFileCn':'FR09','searchCompany':searchCompany,'searchBgnDe':$("#searchBgnDe").val(),'searchEndDe':$("#searchEndDe").val(),'searchBl':$("#searchBl").val(),'searchCnd' :$("#searchCnd").val()};
			var popName = "fileView";
			setPostPopupWin(url,param,popName,0,0,870,800,'auto');
        });
 
		$("a[name='filereg']").click(function(){
			var url = "/cop/bbs/SelectBBSMasterImgPop.do" ;
			var param = {'searchBlNo':$(this).attr("content_id"),'searchBgnDe':$("#searchBgnDe").val(),'searchEndDe':$("#searchEndDe").val(),'searchBl':$("#searchBl").val(),'searchCnd' :$("#searchCnd").val()};
			var popName = "fileUpload";
			setPostPopupWin(url,param,popName,0,0,550,500,'auto');
        });
		
		$("a[name='blreg']").click(function(){
			if (window.confirm("등록된 수기정보를 삭제하시겠습니까?")) {
				var t3_seq = $(this).attr("content_id");
				var t3_yn = $(this).attr("result_id");
				
				$("#t3_seq").val(t3_seq);
				
				document.frm.action = "<c:url value='/cop/bbs/deleteForward.do'/>";
				document.frm.submit();
				
			}			
        });
	
   });
	

   
	function setPostPopupWin(url,params,winnm,winl,wint,nWidth,nHeight,strScroll) {           

			var curX = window.screenX || window.screenLeft || 0;// 현재창의 x좌표 
			var curY = window.screenY || window.screenTop || 0; // 현재창의 y좌표 
			var curHeight = document.body.clientHeight; // 현재창의 높이
			var curWidth = document.body.clientWidth; // 현재창의 너비

			if (winl == 0)
				winl = curX + (curWidth / 2) - (nWidth / 2);

			if (wint == 0)
				wint = curY + (curHeight / 2) - (nHeight / 2);

			if (strScroll == "auto") strScroll = "yes";

			var settings = 'height=' + nHeight + 'px,';
			settings += 'width=' + nWidth + 'px,';
			settings += 'top=' + wint + 'px,';
			settings += 'left=' + winl + 'px,';
			settings += 'scrollbars=' + strScroll + ',';
			settings += 'toolbar=no,location=no,directories=no,status=no,resizable=yes,menubar=no,copyhistory=no';

			var win = window.open("", winnm, settings);

			var $form = $('<form></form>');
			$form.attr('action', url);
			$form.attr('method', 'post');
			$form.attr('target', winnm);
			$form.appendTo('body');
			for (var key in params) {
				var hiddenField = $('<input name="' + key + '" type="hidden" value="' + params[key] + '">');
				$form.append(hiddenField);
			}
			$form.submit();

			if (!win)
				alert('차단된 팝업창을 허용해 주세요.');
			else {
				win.window.resizeTo(nWidth, nHeight);

				if (parseInt(navigator.appVersion) >= 4) { win.window.focus(); }
			}
	}
	
</script>

<style type="text/css">
	h1 {font-size:12px;}
	caption {visibility:hidden; font-size:0; height:0; margin:0; padding:0; line-height:0;}
	
	A:link    { color: #000000; text-decoration:none; }
	A:visited { color: #000000; text-decoration:none; }
	A:active  { color: #000000; text-decoration:none; }
	A:hover   { color: #fa2e2e; text-decoration:none; }	
</style>

</head>



<body>
<noscript class="noScriptTitle">자바스크립트를 지원하지 않는 브라우저에서는 일부 기능을 사용하실 수 없습니다.</noscript>
<!-- 전체 레이어 시작 -->
<div id="wrap">

    <!-- header 시작 -->
    <div id="header"><c:import url="/EgovPageLink.do?link=main/inc/EgovIncHeader" /></div>
    <div id="topnavi"><c:import url="/EgovPageLink.do?link=main/inc/EgovIncTopnav" /></div>        
    <!-- //header 끝 -->


    <!-- container 시작 -->
    <div id="container">
    
        <!-- 좌측메뉴 시작 -->
        <div id="leftmenu"><c:import url="/EgovPageLink.do?link=main/inc/EgovIncLeftmenu" /></div>
        <!-- //좌측메뉴 끝 -->

            <!-- 현재위치 네비게이션 시작 -->
            <div id="content">
                <div id="cur_loc">
                    <div id="cur_loc_align">
                        <ul>
                            <li>구매관리</li>
                            <li>&gt;</li>
                            <li>RPA</li>
                            <li>&gt;</li>
                            <li><strong>수입통관</strong></li>
                        </ul>
                    </div>
                </div>
                
                <!-- 검색 필드 박스 시작 -->
                <div id="search_field"> 
                    <div id="search_field_loc"><h2><strong>수입통관 정보</strong></h2></div>
                    
					
					<form name="frm" id="frm" method="post">
						<!-- 
						<input name="pageIndex" type="hidden" value="<c:out value='${searchVO.pageIndex}'/>"/>
                         -->
                        <input type="hidden" id = "searchBlNo" name="searchBlNo"  />
                        <input type="hidden" id = "regFlag" name="regFlag"  />
                        <input type="hidden" id = "confirmYn"  name="confirmYn" />
                        <input type="hidden" id = "t3_seq"  name="t3_seq" />
                        
                        
                        <fieldset><legend>조건정보 영역</legend>    
                        <div class="sf_start">
                            
                            <ul id="search_first_ul">
                            	<li>
                                <label for="searchBgnDe" >BL일자</label>
                                <input size = "10" maxlength ="8" type="text" id="searchBgnDe" name="searchBgnDe"  value='<c:out value="${searchVO.searchBgnDe}"/>' > ~ 
                                <input size = "10" maxlength ="8" type="text" id="searchEndDe" name="searchEndDe"  value='<c:out value="${searchVO.searchEndDe}" />' >
                                </li>
                                
                                <li>
                                <label for="searchBl" >BL번호</label>
                                <input maxlength="20" type="text" id="searchBl" name="searchBl"   size="13" value='<c:out value="${searchVO.searchBl}"/>' >
                                </li>
                            	
                                <li>
                                    <label for="searchCnd" >확정유무</label>
					                <select id="searchCnd" name="searchCnd" title="검색유형선택" onchange="myFunction(this.value)">
					                   <option value="0" <c:if test="${searchVO.searchCnd == '0'}">selected="selected"</c:if>> == 선택 == </option>
					                   <option value="1" <c:if test="${searchVO.searchCnd == '1'}">selected="selected"</c:if>>확정</option>
					                   <option value="2" <c:if test="${searchVO.searchCnd == '2'}">selected="selected"</c:if>>수기확정</option>
					                   <option value="3" <c:if test="${searchVO.searchCnd == '3'}">selected="selected"</c:if>>확인</option>
					                   <option value="4" <c:if test="${searchVO.searchCnd == '4'}">selected="selected"</c:if>>승인</option>
					                   <option value="5" <c:if test="${searchVO.searchCnd == '5'}">selected="selected"</c:if>>미확정</option>
					                </select>
                                </li>
                            
                                <li>
                                
                                    <div class="buttons" style="position:absolute;left:1510px;top:180px;">
                                       <a href="#" onclick="fn_egov_select_brdMstr2('1'); return false;">
                                       		<img src="<c:url value='/images/img_search.gif' />" alt="search" />조회  </a>
                                    </div>                              
                                </li>
                                
                                <li>
                                 	<div class="buttons" style="position:absolute;left:1590px;top:180px;">
                                       <a href="#" onclick="fn_egov_excel_download('Y'); return false;">확정  </a>
                                    </div>   
                                </li>
                                
                                <li>
                                 	<div class="buttons" style="position:absolute;left:1650px;top:180px;">
                                       <a href="#" onclick="fn_egov_excel_download('N'); return false;">확정취소</a>
                                    </div>   
                                </li>
                                <li>
                                 	<div class="buttons" style="position:absolute;left:1750px;top:180px;">
                                       <a href="#" onclick="fn_egov_forward(); return false;">수기등록</a>
                                    </div>   
                                </li>
                                
                            </ul>
                        </div>          
                        </fieldset>
                    </form>
                    
                    
                    
                    
                    
                </div>
                <!-- //검색 필드 박스 끝 -->

                <!-- div id="page_info"><div id="page_info_align">총 <strong>321</strong>건 (<strong>1</strong> / 12 page)</div></div-->                    
                
                
                <!-- table add start -->
                 <div class="fixed-table-container" id="fix-table" style="width:100%;height:500px;">
        <div class="fixed-table-header-bg"></div>
        <div class="fixed-table-wrapper">
                    
                    <table id="tbl_employee" class="fixed-table">
                    <caption>사용자목록관리</caption>
                    
                    <colgroup>
                    <col width="4%">
                    <col width="7%">  
                    <col width="7%">
                    
                    <col width="5%">
                    <col width="5%">
                    <col width="5%">
                    <col width="8%">
                    
                    <col width="5%">
                    <col width="5%">  
                    <col width="5%">
                    <col width="5%">
                    <col width="5%">
                    <col width="5%">
                    <col width="5%">
                    <col width="5%">
                    <col width="5%">  
                    <col width="4%">
                    
                    <col width="4%">
                    <col width="5%">
                    
                    </colgroup>
                    
                    <thead>
                    <tr>
                        <th rowspan ="2" style="width:4%" ><div class="th-text2">선택</div></th>
                        <th rowspan ="2" style="width:7%" ><div class="th-text2">BL번호</div></th>
                        <th rowspan ="2" style="width:7%" ><div class="th-text2">BL일자</div></th>
                        
                        <th colspan ="4" style="width:26%"><div class="th-text3">통관정보</div></th>
                        <th colspan ="11"style="width:55%"><div class="th-text3">전표</div></th>
                    </tr>
                    <tr>    
                        <th scope="col" style="width:5%"><div class="th-text1" id="import" style="cursor:pointer">INVOICE</div></th>
                        <th scope="col" style="width:5%"><div class="th-text1" id="cargo" style="cursor:pointer">적하보험</div></th>
                        <th scope="col" style="width:5%"><div class="th-text1" id="forward" style="cursor:pointer">BL</div></th>
                        <th scope="col" style="width:8%"><div class="th-text1" id="custom" style="cursor:pointer">수입신고필증</div></th>
                    
                        <th scope="col" style="width:5%"><div class="th-text1">물대</div></th>
                        <th scope="col" style="width:5%"><div class="th-text1">적하보험</div></th>
                        <th scope="col" style="width:5%"><div class="th-text1">관세</div></th>
                        <th scope="col" style="width:5%"><div class="th-text1">취급수수료</div></th>
                        <th scope="col" style="width:5%"><div class="th-text1">운반비</div></th>
                        <th scope="col" style="width:5%"><div class="th-text1">통관수수료</div></th>
                        <th scope="col" style="width:5%"><div class="th-text1">보관료</div></th>
                        
                        <th scope="col" style="width:5%"><div class="th-text1">내륙운송료</div></th>
                        <th scope="col" style="width:5%"><div class="th-text1">이용료</div></th>
                        <th scope="col" style="width:4%"><div class="th-text1">기타</div></th>
                        <th scope="col" style="width:4%"><div class="th-text1">파일등록</div></th>
                        
                    </tr>
                    </thead>
                    
                    
                    
                    <tbody>                 

                    <c:forEach var="result" items="${resultList}" varStatus="status">
                    <!-- loop 시작 -->                                
					  <tr>

					    <td class="lt_text3" nowrap="nowrap" >
					    	<strong><input type="radio"  id="checkBtn${status.count}" name="checkBtn" class="checkBtn" value="${status.count}"></strong>
					    </td>
						<td class ="lt_text3"  nowrap="nowrap">
							<label id="blno${status.count}"><c:out value="${result.t3_blno}" default=""/></label>
							<c:if test = "${result.t3_regflag == 'Y' && result.t3_confirm == 'N'}">
							  	<a name="blreg"  content_id="${result.t3_seq}" result_id="${result.t3_yn}"><img src="/images/delbl.png" height="15" border="0" style="cursor: pointer" /></a>	 
							</c:if>
						</td>
						<td nowrap="nowrap""><label id="bldt${status.count}"><c:out value="${result.t3_sjdt}" default=""/></label></td>
						<td nowrap="nowrap" bgcolor="#${result.t1_color}"><label id="t1_yn${status.count}"><c:out value="${result.t1_yn}" default=""/></label></td>
						<td nowrap="nowrap" bgcolor="#${result.t2_color}"><label id="t2_yn${status.count}"><c:out value="${result.t2_yn}" default=""/></label></td>
						<td nowrap="nowrap" bgcolor="#${result.t3_color}"><label id="t3_yn${status.count}"><c:out value="${result.t3_yn}" default=""/></label></td>
						<td nowrap="nowrap" bgcolor="#${result.t4_color}"><label id="t4_yn${status.count}"><c:out value="${result.t4_yn}" default=""/></label></td>
					   
					    <c:choose>
					    	<c:when test = "${result.c1yn == '0'}">
					    		<td></td>
					    	</c:when>
					    	<c:otherwise>
					    		<td><a name="c1yn"  content_id="${result.t3_blno}" result_id="${result.t3_confirm}"><img src="/images/file.png" height="25" border="0" style="cursor: pointer" /></a></td>
					    	</c:otherwise>
					    </c:choose>
					    <c:choose>
					    	<c:when test = "${result.c2yn == '0'}">
					    		<td></td>
					    	</c:when>
					    	<c:otherwise>
					    		<td><a name="c2yn"  content_id="${result.t3_blno}" result_id="${result.t3_confirm}"><img src="/images/file.png" height="25" border="0" style="cursor: pointer" /></a></td>
					    	</c:otherwise>
					    </c:choose>
					    <c:choose>
					    	<c:when test = "${result.c3yn == '0'}">
					    		<td></td>
					    	</c:when>
					    	<c:otherwise>
					    		<td><a name="c3yn"  content_id="${result.t3_blno}" result_id="${result.t3_confirm}"><img src="/images/file.png" height="25" border="0" style="cursor: pointer" /></a></td>
					    	</c:otherwise>
					    </c:choose>
					    <c:choose>
					    	<c:when test = "${result.c4yn == '0'}">
					    		<td></td>
					    	</c:when>
					    	<c:otherwise>
					    		<td><a name="c4yn"  content_id="${result.t3_blno}" result_id="${result.t3_confirm}"><img src="/images/file.png" height="25" border="0" style="cursor: pointer" /></a></td>
					    	</c:otherwise>
					    </c:choose>
					    <c:choose>
					    	<c:when test = "${result.c5yn == '0'}">
					    		<td></td>
					    	</c:when>
					    	<c:otherwise>
					    		<td><a name="c5yn"  content_id="${result.t3_blno}" result_id="${result.t3_confirm}"><img src="/images/file.png" height="25" border="0" style="cursor: pointer" /></a></td>
					    	</c:otherwise>
					    </c:choose>
					    <c:choose>
					    	<c:when test = "${result.c6yn == '0'}">
					    		<td></td>
					    	</c:when>
					    	<c:otherwise>
					    		<td><a name="c6yn"  content_id="${result.t3_blno}" result_id="${result.t3_confirm}"><img src="/images/file.png" height="25" border="0" style="cursor: pointer" /></a></td>
					    	</c:otherwise>
					    </c:choose>
					    <c:choose>
					    	<c:when test = "${result.c7yn == '0'}">
					    		<td></td>
					    	</c:when>
					    	<c:otherwise>
					    		<td><a name="c7yn"  content_id="${result.t3_blno}" result_id="${result.t3_confirm}"><img src="/images/file.png" height="25" border="0" style="cursor: pointer" /></a></td>
					    	</c:otherwise>
					    </c:choose>
					    <c:choose>
					    	<c:when test = "${result.c8yn == '0'}">
					    		<td></td>
					    	</c:when>
					    	<c:otherwise>
					    		<td><a name="c8yn"  content_id="${result.t3_blno}" result_id="${result.t3_confirm}"><img src="/images/file.png" height="25" border="0" style="cursor: pointer" /></a></td>
					    	</c:otherwise>
					    </c:choose>
					    <c:choose>
					    	<c:when test = "${result.c9yn == '0'}">
					    		<td></td>
					    	</c:when>
					    	<c:otherwise>
					    		<td><a name="c9yn"  content_id="${result.t3_blno}" result_id="${result.t3_confirm}"><img src="/images/file.png" height="25" border="0" style="cursor: pointer" /></a></td>
					    	</c:otherwise>
					    </c:choose>
					    <c:choose>
					    	<c:when test = "${result.c10yn == '0'}">
					    		<td></td>
					    	</c:when>
					    	<c:otherwise>
					    		<td><a name="c10yn"  content_id="${result.t3_blno}" result_id="${result.t3_confirm}"><img src="/images/file.png" height="25" border="0" style="cursor: pointer" /></a></td>
					    	</c:otherwise>
					    </c:choose>
					    <c:choose>
					    	<c:when test = "${result.t3_confirm == 'L'}">
					    		<td></td>
					    	</c:when>
					    	<c:otherwise>
					    		<td><a name="filereg"  content_id="${result.t3_blno}" result_id="Y"><img src="/images/filereg.png" height="25" border="0" style="cursor: pointer" /></a></td>
					    	</c:otherwise>
					    </c:choose>
					    

					    
					    
					    <!-- 
					    <td nowrap="nowrap"><c:out value="${result.t1_yn}"  default=""/></td>
					    <td nowrap="nowrap"><c:out value="${result.t2_yn}" default=""/></td>
					    <td nowrap="nowrap"><c:out value="${result.t3_yn}" default=""/></td>
					    <td nowrap="nowrap"><c:out value="${result.t4_yn}" default=""/></td>
					     -->
					    
					    <!-- 
					    <td nowrap="nowrap">
					    	<c:if test="${result.useAt == 'N'}"><spring:message code="button.notUsed" /></c:if>
					    	<c:if test="${result.useAt == 'Y'}"><spring:message code="button.use" /></c:if>
					    </td>
					     -->  
					  </tr>
	                </c:forEach>	  
					
					<c:if test="${fn:length(resultList) == 0}">
					  <tr>
					    <td nowrap colspan="18"><spring:message code="common.nodata.msg" /></td>  
					  </tr>		 
					</c:if>
			        
			        </tbody>
	                </table>
                </div>
                </div>

		        
		        <!-- 페이지 네비게이션 시작 
		        <!--
		        <div id="paging_div">
                    <ul class="paging_align">
                       <ui:pagination paginationInfo="${paginationInfo}" type="image" jsFunction="fn_egov_select_brdMstr"  />
                    </ul>
		        </div>
		        -->                          
                <!-- //페이지 네비게이션 끝 -->
                
                  
            <!-- //content 끝 -->    
        </div>  
        <!-- //container 끝 -->
	  <!-- 
	  <div id="footer"><c:import url="/EgovPageLink.do?link=main/inc/EgovIncFooter" /></div>  
       -->
       <form id="batchForm" name="batchForm" enctype="multipart/form-data"></form>
       
    </div>
    <!-- //전체 레이어 끝 -->
    <script>
	    // tell the embed parent frame the height of the content
	    if (window.parent && window.parent.parent){
	      window.parent.parent.postMessage(["resultsFrame", {
	        height: document.body.getBoundingClientRect().height,
	        slug: "4jy85xwf"
	      }], "*")
	    }
	
	    $(document).ready(function(){
	    	$('.fixed-table-container').css('height', $(window).height() - 300 );
	    	$(window).resize(function() {
	    		$('.fixed-table-container').css('height', $(window).height() - 300 );
	    	});
	    	
	    	
	    	$("#import").on('click', function() {
	            if (window.confirm("수입신고 엑셀(*.xls)을 업로드하시겠습니까?")){
	            	$("#batchForm").ajaxForm({
	                    type: 'POST',
	                    url: "/excel/processImport.do",
	                    dataType: "json",
	                    enctype: "multipart/form-data",
	                    contentType: false,
	                    processData: false,
	                    timeout: 30000,
	                    success: function(result) {
	                        if (result.status == 1) {
	                            alert(result.msg);
	                            document.frm.action = "<c:url value='/cop/bbs/SelectBBSMasterInfs.do'/>";
	                			document.frm.submit();

	                        }
	                        else if (result.status == 0) {
	                            alert(result.msg);
	                        }
	                    },
	                    error: function(data, status, err) {
	                        alert("서버가 응답하지 않습니다." + "\n" + "다시 시도해주시기 바랍니다." + "\n"
	                            + "code: " + data.status + "\n"
	                            + "message :" + data.responseText + "\n"
	                            + "message1 : " + status + "\n"
	                            + "error: " + err);
	                    }
	                }).submit();
	            }
	            else{
	            	return;
	            }

	        });
	    	
	    	$("#cargo").on('click', function() {
				if (window.confirm("적화보험 엑셀(*.xls)을 업로드하시겠습니까?")){
					$("#batchForm").ajaxForm({
	                    type: 'POST',
	                    url: "/excel/processCargo.do",
	                    dataType: "json",
	                    enctype: "multipart/form-data",
	                    contentType: false,
	                    processData: false,
	                    timeout: 30000,
	                    success: function(result) {
	                        if (result.status == 1) {
	                            alert(result.msg);
	                            document.frm.action = "<c:url value='/cop/bbs/SelectBBSMasterInfs.do'/>";
	                			document.frm.submit();

	                        }
	                        else if (result.status == 0) {
	                            alert(result.msg);
	                        }
	                    },
	                    error: function(data, status, err) {
	                        alert("서버가 응답하지 않습니다." + "\n" + "다시 시도해주시기 바랍니다." + "\n"
	                            + "code: " + data.status + "\n"
	                            + "message :" + data.responseText + "\n"
	                            + "message1 : " + status + "\n"
	                            + "error: " + err);
	                    }
	                }).submit();
	            }
	            else{
	            	return;
	            }

	        });
	    	
	    	$("#forward").on('click', function() {
				if (window.confirm("포워더 엑셀(*.xls)을 업로드하시겠습니까?")){
					$("#batchForm").ajaxForm({
	                    type: 'POST',
	                    url: "/excel/processForward.do",
	                    dataType: "json",
	                    enctype: "multipart/form-data",
	                    contentType: false,
	                    processData: false,
	                    timeout: 30000,
	                    success: function(result) {
	                        if (result.status == 1) {
	                            alert(result.msg);
	                            document.frm.action = "<c:url value='/cop/bbs/SelectBBSMasterInfs.do'/>";
	                			document.frm.submit();

	                        }
	                        else if (result.status == 0) {
	                            alert(result.msg);
	                        }
	                    },
	                    error: function(data, status, err) {
	                        alert("서버가 응답하지 않습니다." + "\n" + "다시 시도해주시기 바랍니다." + "\n"
	                            + "code: " + data.status + "\n"
	                            + "message :" + data.responseText + "\n"
	                            + "message1 : " + status + "\n"
	                            + "error: " + err);
	                    }
	                }).submit();
	            }
	            else{
	            	return;
	            }

	        });
	    	
	    	$("#custom").on('click', function() {
				if (window.confirm("관세사 엑셀(*.xlsx)을 업로드하시겠습니까?")){
					$("#batchForm").ajaxForm({
	                    type: 'POST',
	                    url: "/excel/processCustom.do",
	                    dataType: "json",
	                    enctype: "multipart/form-data",
	                    contentType: false,
	                    processData: false,
	                    timeout: 30000,
	                    success: function(result) {
	                        if (result.status == 1) {
	                            alert(result.msg);
	                            document.frm.action = "<c:url value='/cop/bbs/SelectBBSMasterInfs.do'/>";
	                			document.frm.submit();

	                        }
	                        else if (result.status == 0) {
	                            alert(result.msg);
	                        }
	                    },
	                    error: function(data, status, err) {
	                        alert("서버가 응답하지 않습니다." + "\n" + "다시 시도해주시기 바랍니다." + "\n"
	                            + "code: " + data.status + "\n"
	                            + "message :" + data.responseText + "\n"
	                            + "message1 : " + status + "\n"
	                            + "error: " + err);
	                    }
	                }).submit();
	            }
	            else{
	            	return;
	            }
	        });
	    });	
	    
	    // always overwrite window.name, in case users try to set it manually
	    window.name = "result"
	  </script>

    
 </body>
</html>
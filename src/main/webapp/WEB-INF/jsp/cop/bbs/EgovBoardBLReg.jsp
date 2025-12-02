<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmmss");
    String today = formatter1.format(new Date());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<c:url value='/'/>css/common.css?<%=today%>" rel="stylesheet" type="text/css" >

<script type="text/javascript" src="<c:url value='/js/EgovBBSMng.js' />"></script>
<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.js" ></script>

<script src="http://code.jquery.com/jquery-1.10.2.js"></script>
<script src="http://code.jquery.com/ui/1.11.2/jquery-ui.js"></script>
<script type="text/javascript" src="<c:url value='/js/egovframework/com/cmm/fms/EgovMultiFile.js'/>" ></script>
<script src="https://malsup.github.io/jquery.form.js"></script>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">

<link type="text/css" rel="stylesheet" href="<c:url value='/css/egovframework/com/com.css?<%=today%>' />">

<style>
/*datepicer 버튼 롤오버 시 손가락 모양 표시*/
.ui-datepicker-trigger{cursor: pointer;}
/*datepicer input 롤오버 시 손가락 모양 표시*/
.hasDatepicker{cursor: pointer;}

</style>

<script type="text/javascript">
	
   $(function(){
	   $("#t3_sjdt").datepicker({
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
	
   });

	
</script>
</head>
<body>

	<div id="search_field" style="width:450px"> 
          	<div id="search_field_loc"><h2><strong>통관정보 파일 업로드</strong></h2></div>
          </div>
		<form id="frm" name="frm" enctype="multipart/form-data">
	        <table width="400px"  style="margin-left: auto; margin-right: auto;border-collapse: separate;  border-spacing: 0 10px;border-bottom: 1px solid #fff;">
	        	<colgroup>
					<col width="150px">
					<col width="*">  
				</colgroup>
	               <tr style="border-bottom: 1px solid #000;">
	               		<td style="text-align:left">I/N No.</td>
	                   <td style="text-align:left"><input type="text" name="t3_inno" id="t3_inno"  /></td>
	               </tr>
	               <tr style="border-bottom: 1px solid #000;">
	               		<td style="text-align:left">B/L No.</td>
	                   <td style="text-align:left"><input type="text" name="t3_blno" id="t3_blno"  /></td>
	               </tr>
	               <tr style="border-bottom: 1px solid #000;">
	               		<td style="text-align:left">B/L 일자.</td>
	                   <td style="text-align:left"><input type="text" name="t3_sjdt" id="t3_sjdt"  /></td>
	               </tr>
	               <tr>
                   <td colspan="2" align="center">
                          <input type="button" id="signUpBtn" value="저장"> &nbsp; <input type="button" id="btnClose" value="닫기">
                   </td>
	           </tr>
	        </table>
	 </form>
	 
	 <form name="myForm" id="myForm" method="post">
	    <input type="hidden" name="searchBgnDe" id="searchBgnDe" value='<c:out value="${searchVO.searchBgnDe}"/>' />
	    <input type="hidden" name="searchEndDe" id="searchEndDe" value='<c:out value="${searchVO.searchEndDe}"/>' />
	    <input type="hidden" name="searchBl" id="searchBl" value='<c:out value="${searchVO.searchBl}"/>' />
	    <input type="hidden" name="searchCnd" id="searchCnd" value='<c:out value="${searchVO.searchCnd}"/>' />
	</form>

<script type="text/javascript">
	$(function() {
		
		$("input[name=fileSn][value=${searchVO.searchFileCn}]").prop("checked",true);

	    $("#signUpBtn").unbind("click").click(function(e) {
	        e.preventDefault();
	        fn_signUp();
	    });
	
	    $("#btnClose").unbind("click").click(function(e) {
	    	self.opener = self;
			    
			window.close();
	    });
	
	});
	
	function fn_signUp() {

	    if ($("#t3_inno").val() == "")
	    {
	        alert("I/N No.를 입력해주세요.");
	        $("#t3_inno").focus();
	        return;
	    }
	    if ($("#t3_blno").val() == "")
	    {
	        alert("B/L No.를 입력해주세요.");
	        $("#t3_blno").focus();
	        return;
	    }
	    if ($("#t3_sjdt").val() == "")
	    {
	        alert("B/L일자를 입력해주세요.");
	        $("#t3_sjdt").focus();
	        return;
	    }

        if (window.confirm("수기 등록하시겠습니까?")) {
            $("#frm").ajaxForm({
                type: 'POST',
                url: '<c:url value='/cop/bbs/insertForward.do'/>',
                dataType: "json",
                enctype: "multipart/form-data",
                contentType: false,
                processData: false,
                timeout: 50000,
                success: function(result) {
                	if (result.status == 0) {
                        alert(result.msg);
                        document.myForm.method = "post";
                	    document.myForm.action = "/cop/bbs/SelectBBSMasterInfs.do";
                	    document.myForm.target = opener.window.name; 

                	    document.myForm.submit();
                        //window.opener.$("#myForm").attr("target", "result");
            	    	//window.opener.$("#myForm").attr("action", "/cop/bbs/SelectBBSMasterInfs.do");
            	    	//window.opener.$("#myForm").submit();
                        
            			window.close();
                    }
                    else if (result.status == 1) {
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
	

	}        
</script>


</body>

</html> 

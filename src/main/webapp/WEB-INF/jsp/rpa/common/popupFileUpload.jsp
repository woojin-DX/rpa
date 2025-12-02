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
</head>
<body>

			<div id="search_field" style="width:450px"> 
            	<div id="search_field_loc"><h2><strong>부가세 관련 파일 업로드</strong></h2></div>
            </div>
				<form id="frm" name="frm" enctype="multipart/form-data">
			        <input type="hidden" name="savePath" value="Globals.fileStorePath" />
			        <input type="hidden" name="saveDir" value="extra" />
			        <input type="hidden" name="blNo" value='<c:out value="${searchVO.searchBlNo}"/>' />
			        <input type="hidden" name="searchFileCn" value='<c:out value="${searchVO.searchFileCn}"/>' />
			        <input type="hidden" name="fileGubun" id="fileGubun" />
			        <input type="hidden" name="fileGubunNm" id="fileGubunNm" />
			        <table width="400px"  style="margin-left: auto; margin-right: auto;border-collapse: separate;  border-spacing: 0 10px;border-bottom: 10px solid #fff;">
			               <tr>
			                   <td style="text-align:left"><input type="checkbox" name="fileSn" id="sta0" value="STA0" /><label for="sta0">세금계산서</label></td>
			               </tr>
			               <tr>
			                   <td style="text-align:left"><input type="checkbox" name="fileSn" id="sta1" value="STA1" /><label for="sta1">거래명세서</label></td>
			               </tr>
			               <tr>
			                   <td style="text-align:left"><input type="checkbox" name="fileSn" id="sta2" value="STA2" /><label for="sta2">기타</label></td>
			               </tr>
			               <tr>
			                   <td style="text-align:left" >
			                        <input type="file" name="file1" id="file1" /><br>
			                   </td>
			               </tr>
			               <tr>
			                       <td align="center">
			                              <input type="button" id="signUpBtn" value="저장"> &nbsp; <input type="button" id="btnClose" value="닫기">
			                       </td>
			           </tr>
			        </table>
			 </form>
			 
			 <form name="myForm" id="myForm" method="post">
			    <input type="hidden" name="searchBgnDe" id="searchBgnDe" value='<c:out value="${searchVO.searchBgnDe}"/>' />
			    <input type="hidden" name="searchEndDe" id="searchEndDe" value='<c:out value="${searchVO.searchEndDe}"/>' />
			    <input type="hidden" name="searchGubun" id="searchGubun" value='<c:out value="${searchVO.searchGubun}"/>' />
			    <input type="hidden" name="searchConfirm" id="searchConfirm" value='<c:out value="${searchVO.searchConfirm}"/>' />
			    <input type="hidden" name="searchCom" id="searchCom" value='<c:out value="${searchVO.searchCom}"/>' />
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
		var chkCnt = $("input[name='fileSn']:checked").length;

		if (chkCnt < 1)
	    {
	        alert("등록할 파일의 구분은 하나이상 체크하셔야 합니다.");
	        return;
	    }
	    if ($("#file1").val() == "")
	    {
	        alert("파일을 입력해주세요.");
	        $("#file1").focus();
	        return;
	    }
    	var items=[]; 
    	$('input[name="fileSn"]:checkbox:checked').each(function(){items.push($(this).val());}); 
    	var tmp = items.join(',');
    	$("#fileGubun").val(tmp);
    	
    	var texts=[]; 
    	$('input[name="fileSn"]:checkbox:checked').each(function(){texts.push($(this).next('label').text());}); 

    	var txt = texts.join(',');
    	$("#fileGubunNm").val(txt);

        if (window.confirm("파일을 등록하시겠습니까?")) {
            $("#frm").ajaxForm({
                type: 'POST',
                url: '<c:url value='/file/fileUploadSap.do'/>',
                dataType: "json",
                enctype: "multipart/form-data",
                contentType: false,
                processData: false,
                timeout: 50000,
                success: function(result) {
                	if (result.status == 0) {
                        alert(result.msg);
                        document.myForm.method = "post";
                	    document.myForm.action = "/cop/bbs/selectExtraSubList.do";
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

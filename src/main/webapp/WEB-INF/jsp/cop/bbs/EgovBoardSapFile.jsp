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
<script src="http://malsup.github.com/jquery.form.js"></script>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">

<link type="text/css" rel="stylesheet" href="<c:url value='/css/egovframework/com/com.css?<%=today%>' />">
</head>
<body>

			<div id="search_field" style="width:450px"> 
            	<div id="search_field_loc"><h2><strong>통관정보 파일 업로드</strong></h2></div>
            </div>
				<form id="frm" name="frm" enctype="multipart/form-data">
			        <input type="hidden" name="savePath" value="Globals.fileStorePath" />
			        <input type="hidden" name="saveDir" value="sap" />
			        <input type="hidden" name="blNo" value='<c:out value="${searchVO.searchBlNo}"/>' />
			        <input type="hidden" name="searchFileCn" value='<c:out value="${searchVO.searchFileCn}"/>' />
			        <input type="hidden" name="fileGubun" id="fileGubun" />
			        <input type="hidden" name="fileGubunNm" id="fileGubunNm" />
			        <table width="400px"  style="margin-left: auto; margin-right: auto;border-collapse: separate;  border-spacing: 0 10px;border-bottom: 10px solid #fff;">
			               <tr>
			                   <td style="text-align:left"><input type="checkbox" name="fileSn" id="c1yn" value="FR00" /><label for="c1yn">물대</label></td>
			               </tr>
			               <tr>
			                   <td style="text-align:left"><input type="checkbox" name="fileSn" id="c5yn" value="FR01" /><label for="c5yn">운반비</label></td>
			               </tr>
			               <tr>
			                   <td style="text-align:left"><input type="checkbox" name="fileSn" id="c3yn" value="FR02" /><label for="c3yn">관세</label></td>
			               </tr>
			               <tr>
			                   <td style="text-align:left"><input type="checkbox" name="fileSn" id="c6yn" value="FR03" /><label for="c6yn">통관수수료</label></td>
			               </tr>
			               <tr>
			                   <td style="text-align:left"><input type="checkbox" name="fileSn" id="c4yn" value="FR04" /><label for="c4yn">취급수수료</label></td>
			               </tr>
			               <tr>
			                   <td style="text-align:left"><input type="checkbox" name="fileSn" id="c7yn" value="FR05" /><label for="c7yn">보관료</label></td>
			               </tr>
			               <tr>
			                   <td style="text-align:left"><input type="checkbox" name="fileSn" id="c2yn" value="FR06" /><label for="c2yn">적하보험</label></td>
			               </tr>
			               <tr>
			                   <td style="text-align:left"><input type="checkbox" name="fileSn" id="c9yn" value="FR07" /><label for="c9yn">이용료</label></td>
			               </tr>
			               <tr>
			                   <td style="text-align:left"><input type="checkbox" name="fileSn" id="c8yn" value="FR08" /><label for="c8yn">내륙운송료</label></td>
			               </tr>
			               <tr>
			                   <td style="text-align:left"><input type="checkbox" name="fileSn" id="c10yn" value="FR09" /><label for="c10yn">기타</label></td>
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
            /* 2025-12-02 변경
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
            }).submit();*/
        	var formData = new FormData($("#frm")[0]);

        	$.ajax({
        	    type: 'POST',
        	    url: '<c:url value="/file/fileUploadSap.do"/>',
        	    data: formData,
        	    dataType: "json",
        	    enctype: "multipart/form-data",
        	    contentType: false,
        	    processData: false,
        	    success: function(result) {
        	        if (result.status == 0) {
        	            alert(result.msg);
        	            document.myForm.method = "post";
        	            document.myForm.action = "/cop/bbs/SelectBBSMasterInfs.do";
        	            document.myForm.target = opener.window.name;
        	            document.myForm.submit();
        	            window.close();
        	        } else if (result.status == 1) {
        	            alert(result.msg);
        	        }
        	    },
        	    error: function(data, status, err) {
        	        alert("서버가 응답하지 않습니다.\n다시 시도해주시기 바랍니다.\n"
        	            + "code: " + data.status + "\n"
        	            + "message :" + data.responseText + "\n"
        	            + "message1 : " + status + "\n"
        	            + "error: " + err);
        	    }
        	});

        }
	

	}        
</script>


</body>

</html> 

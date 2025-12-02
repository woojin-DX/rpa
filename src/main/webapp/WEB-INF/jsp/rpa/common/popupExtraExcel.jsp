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

<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.js" ></script>

<script src="http://code.jquery.com/jquery-1.10.2.js"></script>
<script src="http://code.jquery.com/ui/1.11.2/jquery-ui.js"></script>
<script src="https://malsup.github.io/jquery.form.js"></script>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
</head>
<body>

			<div id="search_field" style="width:450px;text-align: center;"> 
            	<div id="search_field_loc"><h1><strong>부가세액 파일 업로드</strong></h1></div>
            </div>
            
				<form id="frm" name="frm" enctype="multipart/form-data">
			        <input type="hidden" name="savePath" value="Globals.fileStorePath" />
			        <input type="hidden" name="regYearMon" id="regYearMon" value='<c:out value="${searchVO.searchMonth}"/>' />
			        <table width="400px"  style="margin-left: auto; margin-right: auto;border-collapse: separate;  border-spacing: 10px 10px;border:1px solid #DDDDDD;">
			        <caption>사용자목록관리</caption>
                    
                    <colgroup>
                    <col width="20%">
                    <col width="*">
                    </colgroup>
			               <tr>
			                   <td style="text-align:left;" colspan="2"><label style="color:red">*부가세액은 엑셀파일만 업로드 가능합니다</label></td>
			               </tr>
			               
			               <tr>
			                   <th style="text-align:left" >전기일자</th>
			                   <td style="text-align:left"  ><c:out value="${searchVO.searchBgnDe}"/> ~ <c:out value="${searchVO.searchEndDe}"/><br>
			                   </td>
			               </tr>
			               
			               <tr>
			                   <td style="text-align:left" >파일등록</td>
			                   <td style="text-align:left"  >
			                        <input type="file" name="file1" id="file1" accept=".xls, .xlsx" /><br>
			                   </td>
			               </tr>
			               <tr>
			                       <td align="center" colspan="2">
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
	    
	    if( $("#file1").val() != "" ){
	    	var ext = $('#file1').val().split('.').pop().toLowerCase();
    	    if($.inArray(ext, ['xls','xlsx']) == -1) {
    			alert('xls,xlsx 파일만 업로드 할수 있습니다.');
    			return;
    	    }
    	}
	    else{
	    	alert("파일을 입력해주세요.");
	        $("#file1").focus();
	        return;
	    }


        if (window.confirm("파일을 등록하시겠습니까?")) {
            $("#frm").ajaxForm({
                type: 'POST',
                url: '<c:url value='/file/fileUploadExtra.do'/>',
                dataType: "json",
                enctype: "multipart/form-data",
                contentType: false,
                processData: false,
                timeout: 50000,
                success: function(result) {
                	if (result.status == 0) {
                        alert(result.msg);
                        document.myForm.method = "post";
                	    document.myForm.action = "/rpa/cmm/selectExtraSubList.do";
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

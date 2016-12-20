<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String path = request.getContextPath();
%>

<!DOCTYPE html>
<html lang="UTF-8">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
<meta name="renderer" content="webkit">
<title></title>
<link rel="stylesheet" href="<%=path%>/css/pintuer.css">
<link rel="stylesheet" href="<%=path%>/css/admin.css">
<script src="<%=path%>/js/jquery.js"></script>
<script src="<%=path%>/js/pintuer.js"></script>
</head>
<body>
	<div class="panel admin-panel">
		<div class="panel-head">
			<strong class="icon-reorder">推荐动态列表</strong>
		</div>
		<div class="padding border-bottom">
			<button type="button" class="button border-yellow"
				onclick="window.location.href='#add'">
				<span class="icon-plus-square-o"></span>添加
			</button>
		</div>
		<table class="table table-hover text-center">
			<tr>
				<th width="10%">ID</th>
				<th width="20%">文字内容</th>
				<th width="35%">图片</th>
				<th width="5%">发布者</th>
				<th width="10%">发布时间</th>
				<th width="5%">点赞次数</th>
				<th width="5%">评论次数</th>
				<th width="10%">管理</th>
			</tr>
			<c:forEach var="dy" items="${selecteds}">
				<tr>
					<td>${dy.id }</td>
					<td>${dy.description }</td>
					<td><img src="${dy.thumb }" alt="" width="120" height="50" /></td>
					<td>${dy.user.nick_name }</td>
					<td>${dy.create_time }</td>
					<td>0</td>
					<td>0</td>
					<td><div class="button-group">
							<a class="button border-red" href="javascript:void(0)"
								onclick="return del(1,1)"><span class="icon-trash-o"></span>
								删除</a>
						</div></td>
				</tr>
			</c:forEach>
		</table>
	</div>
	<script type="text/javascript">
		function del(id, mid) {
			if (confirm("æ¨ç¡®å®è¦å é¤å?")) {

			}
		}
	</script>
	<div class="panel admin-panel margin-top" id="add">
		<div class="panel-head">
			<strong><span class="icon-pencil-square-o"></span>
				å¢å åå®¹</strong>
		</div>
		<div class="body-content">
			<form method="post" class="form-x" action="">
				<div class="form-group">
					<div class="label">
						<label>æ é¢ï¼</label>
					</div>
					<div class="field">
						<input type="text" class="input w50" value="" name="title"
							data-validate="required:è¯·è¾å¥æ é¢" />
						<div class="tips"></div>
					</div>
				</div>
				<div class="form-group">
					<div class="label">
						<label>URLï¼</label>
					</div>
					<div class="field">
						<input type="text" class="input w50" name="url" value="" />
						<div class="tips"></div>
					</div>
				</div>
				<div class="form-group">
					<div class="label">
						<label>å¾çï¼</label>
					</div>
					<div class="field">
						<input type="text" id="url1" name="img" class="input tips"
							style="width: 25%; float: left;" value="" data-toggle="hover"
							data-place="right" data-image="" /> <input type="button"
							class="button bg-blue margin-left" id="image1"
							value="+ æµè§ä¸ä¼ " style="float: left;">
						<div class="tipss">å¾çå°ºå¯¸ï¼1920*500</div>
					</div>
				</div>
				<div class="form-group">
					<div class="label">
						<label>æè¿°ï¼</label>
					</div>
					<div class="field">
						<textarea type="text" class="input" name="note"
							style="height: 120px;" value=""></textarea>
						<div class="tips"></div>
					</div>
				</div>
				<div class="form-group">
					<div class="label">
						<label>æåºï¼</label>
					</div>
					<div class="field">
						<input type="text" class="input w50" name="sort" value="0"
							data-validate="required:,number:æåºå¿é¡»ä¸ºæ°å­" />
						<div class="tips"></div>
					</div>
				</div>
				<div class="form-group">
					<div class="label">
						<label></label>
					</div>
					<div class="field">
						<button class="button bg-main icon-check-square-o" type="submit">
							æäº¤</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</body>
</html>
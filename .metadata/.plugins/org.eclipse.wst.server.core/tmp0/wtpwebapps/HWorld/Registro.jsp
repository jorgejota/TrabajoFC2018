<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Artículos Neurocientificos</title>
<style>
body {
	background-color: #d5f4e6;
}

td {
	padding: 5px 0;
}
</style>
</head>
<body>
	<h1 style="text-align: center">Selecciona los siguientes datos:</h1>
	<form action="VistaJSP.jsp" method="post"> <!-- Puede ser que sea get ?? -->
		<table width="25%"> 
			<tr>
				<td width="13%"><label for="keyW">KeyWord: </label></td>
				<td width="87%"><input type="text" name="keyW" id="keyW"></td>
			</tr>
			<tr>
				<td>WebSites:</td>
				<td><label> <input type="checkbox" name="tecnologias"
						value="Zenodo" id="web_0"> Zenodo
				</label> <br> <label> <input type="checkbox" name="tecnologias"
						value="Darksky" id="web_1"> Darksky
				</label>
			</tr>
			<tr>
				<td colspan="2" align="center"><input type="submit"
					name="button" id="button" value="Enviar"></td>
			</tr>
		</table>
		<p>
			<br>
		</p>
	</form>
</body>
</html>

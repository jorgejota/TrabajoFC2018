<html>
<head>
<style>
.thumb {
	height: 300px;
	border: 1px solid #000;
	margin: 10px 5px 0 0;
}

div#wrapperFooter {
	width: 100%;
	height: 10%; /* height of the background image? */
	text-align: center;
}

div#wrapperFooter div#footer {
	width: 100%;
	height: 100%;
	margin: 0 auto;
}
</style>
</head>
<body>
	<input type="file" id="files" name="files[]" />
	<br />
	<output id="list"></output>

	<script>
              function archivo(evt) {
                  var files = evt.target.files; // FileList object
             
                  // Obtenemos la imagen del campo "file".
                  for (var i = 0, f; f = files[i]; i++) {
                    //Solo admitimos imágenes.
                    if (!f.type.match('image.*')) {
                        continue;
                    }
             
                    var reader = new FileReader();
             
                    reader.onload = (function(theFile) {
                        return function(e) {
                          // Insertamos la imagen
                         document.getElementById("list").innerHTML = ['<img class="thumb" src="', e.target.result,'" title="', escape(theFile.name), '"/>'].join('');
                        };
                    })(f);
             
                    reader.readAsDataURL(f);
                  }
              }
             
              document.getElementById('files').addEventListener('change', archivo, false);
      </script>

	<div id="wrapperFooter">
		<div id="footer">
			<a href="http://www.oeg-upm.net"> <img src="footer.png" alt="oeg" />
			</a>
		</div>
	</div>
</body>
</html>
<html>

<head>
<style type="text/css">
  html { box-sizing: border-box; }

*,
*::before,
*::after { box-sizing: inherit; }

body * {
  margin: 0;
  padding: 0;
}

body {
  font: normal 100%/1.15 "Merriweather", serif;
  background-color: #7ed0f2;
  color: #fff;
}

.wrapper {
  position: relative;
  max-width: 1298px;
  height: auto;
  margin: 0em auto 0 auto;
}

.box {
  max-width: 70%;
  min-height: auto;
  margin: 0 auto;
  padding: 1em 1em;
  text-align: center;
  background: url("https://www.dropbox.com/s/xq0841cp3icnuqd/flame.png?raw=1") no-repeat top 10em center/128px 128px,
              transparent url("https://www.dropbox.com/s/w7qqrcvhlx3pwnf/desktop-pc.png?raw=1") no-repeat top 13em center/128px 128px;
}

h1, p:not(:last-of-type) { text-shadow: 0 0 6px #216f79; }

h1 {
  margin: 0 0 1rem 0;
  font-size: 8em;
}

p {
  margin-bottom: 0.5em;
  font-size: 2.8em;
}

p:first-of-type { margin-top: 4em; }

p img { vertical-align: bottom; }

</style>

</head>

<body>

<div class="wrapper">
<div class="box">
<h1>500</h1>
<p>Internal Error</p>
<p>${error}</p>
</div>
</div>

</body>

</html>
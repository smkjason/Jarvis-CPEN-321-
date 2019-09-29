var XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest

const Http = new XMLHttpRequest();
const url='http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com';
Http.open("GET", url);
Http.send();

Http.onreadystatechange = (e) => {
  console.log(Http.responseText)
}
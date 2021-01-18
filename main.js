function idName(id) {
  return document.getElementById(String(id));
}

// Call this function whenever you wanna swap to other screen

function showHide(divOne, divTwo) {
  var x = idName(divOne);
  var y = idName(divTwo);
  if (x.style.display === "none") {
    x.style.display = "inline-block";
  } else {
    x.style.display = "none";
  }
  if (y.style.display === "none") {
    y.style.display = "inline-block";
  } else {
    y.style.display = "none";
  }
}

function compoundShowHide(divOne, divOneContent, divTwo, divTwoContent) {
  var x1 = idName(divOne);
  var x2 = idName(divOneContent);
  var y1 = idName(divTwo);
  var y2 = idName(divTwoContent);
  if (x1.style.display === "none") {
    x1.style.display = "inline-block";
    x2.style.display = "inline-block";
  } else {
    x1.style.display = "none";
    x2.style.display = "none";
  }
  if (y1.style.display === "none") {
    y1.style.display = "inline-block";
    y2.style.display = "inline-block";
  } else {
    y1.style.display = "none";
    y2.style.display = "none";
  }
}

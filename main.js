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
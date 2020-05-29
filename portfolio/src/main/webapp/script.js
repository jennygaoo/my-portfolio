function randomRecipe() {
  // there are three recipes total
  const imageIndex = Math.floor(Math.random() * 4) + 1;
  const imgUrl = "/images/recipes/" + imageIndex + ".jpg";

  const imgElement = document.createElement("img");
  imgElement.src = imgUrl;
  const imageContainer = document.getElementById("random-image-container");
  
  if(imageIndex == 1){
    const str = "ba's best chocolate chip cookies";
    const result = str.link("https://www.bonappetit.com/recipe/bas-best-chocolate-chip-cookies");
    document.getElementById("recipe-link").innerHTML = result;
  }
  if(imageIndex == 2){
    const str = "queer eye's antoni's lemon squares";
    const result = str.link("https://beta.theloop.ca/food/recipes/salty-lemon-squares.html");
    document.getElementById("recipe-link").innerHTML = result;
  }
  if(imageIndex == 3){
    const str = "pasta al limone";
    const result = str.link("https://www.bonappetit.com/recipe/pasta-al-limone");
    document.getElementById("recipe-link").innerHTML = result;
  }
  if(imageIndex == 4){
    const str = "basque burnt cheesecake";
    const result = str.link("https://www.bonappetit.com/recipe/basque-burnt-cheesecake");
    document.getElementById("recipe-link").innerHTML = result;
  }

  imageContainer.innerHTML = '';
  imageContainer.appendChild(imgElement);
}

function randomRecipe() {
  // there are three recipes total
  const imageIndex = Math.floor(Math.random() * 3) + 1;
  const imgUrl = '/images/recipes/' + imageIndex + '.jpg';

  const imgElement = document.createElement('img');
  imgElement.src = imgUrl;

  const imageContainer = document.getElementById('random-image-container');
 
  imageContainer.innerHTML = '';
  imageContainer.appendChild(imgElement);
}

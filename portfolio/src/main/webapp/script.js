// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

function randomizeRecipe() {
  // there are 4 recipes total
  const imageIndex = Math.floor(Math.random() * 4) + 1;
  const imgPath = "/images/recipes/" + imageIndex + ".jpg";

  const imgElement = document.createElement("img");
  imgElement.src = imgPath;
  const imageContainer = document.getElementById("random-image-container");

  if (imageIndex === 1) {
    const str = "ba's best chocolate chip cookies";
    const result = str.link("https://www.bonappetit.com/recipe/bas-best-chocolate-chip-cookies");
    document.getElementById("recipe-link").innerHTML = result;
  } else if (imageIndex === 2) {
    const str = "queer eye's antoni's lemon squares";
    const result = str.link("https://beta.theloop.ca/food/recipes/salty-lemon-squares.html");
    document.getElementById("recipe-link").innerHTML = result;
  } else if (imageIndex === 3) {
    const str = "pasta al limone";
    const result = str.link("https://www.bonappetit.com/recipe/pasta-al-limone"); 
    document.getElementById("recipe-link").innerHTML = result;
  } else if (imageIndex === 4) {
    const str = "basque burnt cheesecake";
    const result = str.link("https://www.bonappetit.com/recipe/basque-burnt-cheesecake");
    document.getElementById("recipe-link").innerHTML = result;
  } 

  imageContainer.innerHTML = "";
  imageContainer.appendChild(imgElement);
}

async function getComments() {
  const response = await fetch("/data");
  const comments = await response.json();
  const commentsElement = document.getElementById("comments");
  comments.forEach((comment) => {
    commentsElement.appendChild(createCommentsElement(comment));
  });
}

function createCommentsElement(comment) {
  const commentElement = document.createElement("li");
  commentElement.className = "comment";

  const contentElement = document.createElement('span');
  contentElement.innerText = comment.content;

  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.addEventListener('click', () => {
    deleteComment(comment);

    commentElement.remove();
  });

  commentElement.appendChild(contentElement);
  commentElement.appendChild(deleteButtonElement);
  return commentElement;
}

function deleteComment(comment) {
  const params = new URLSearchParams();
  params.append('id', comment.id);
  fetch('/delete-comment', {method: 'POST', body: params});
}

function loadMap() {
  var sanFrancisco = {lat: 37.7749, lng: -122.4194};
  const cafeMap = new google.maps.Map(
    document.getElementById("map"),
    {center: sanFrancisco, zoom: 12});

  const philzMarker = new google.maps.Marker({
    position: {lat: 37.793949, lng: -122.398062},
    map: cafeMap, 
    title: "Philz Coffee"
  });

  const sFrankMarker = new google.maps.Marker({
    position: {lat:	37.779511, lng: -122.410410608696},
    map: cafeMap, 
    title: "Saint Frank Coffee"
  });

  const sMatchaMarker = new google.maps.Marker({
    position: {lat:	37.764730, lng: -122.421731},
    map: cafeMap, 
    title: "Stonemill Matcha"
  });

  const fBarrelMarker = new google.maps.Marker({
    position: {lat:	37.768055, lng: -122.422117},
    map: cafeMap, 
    title: "Four Barrel Coffee"
  });

}

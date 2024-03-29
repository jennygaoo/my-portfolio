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

let editMarker, model, webcam, labelContainer, maxPredictions;
const URL = "https://teachablemachine.withgoogle.com/models/mSTKdNc_2/";

// load image model, set up webcam
async function predictImage() {
  const modelURL = URL + "model.json";
  const metadataURL = URL + "metadata.json";

  // load model, metadata
  model = await tmImage.load(modelURL, metadataURL);
  // Model can predict what is seen by webcam
  // either as Squidward or Spongebob
  maxPredictions = model.getTotalClasses();

  // set up webcam
  const flip = true; // whether to flip the webcam
  webcam = new tmImage.Webcam(/* width= */ 200, /* height= */ 200, flip);
  await webcam.setup(); // request access to the webcam
  await webcam.play();
  window.requestAnimationFrame(updateAndPredict);

  // append webcam and prediction to DOM
  document.getElementById("webcam-container").appendChild(webcam.canvas);
  labelContainer = document.getElementById("image-prediction");
}

async function updateAndPredict() {
  webcam.update();
  await predict();
  window.requestAnimationFrame(updateAndPredict);
}

async function predict() {
  const prediction = await model.predict(webcam.canvas);
  for (let i = 0; i < maxPredictions; i++) {
    labelContainer.appendChild(document.createElement("div"));
    const classPrediction = 
        prediction[i].className + ": " + prediction[i].probability.toFixed(2);
    labelContainer.childNodes[i].innerHTML = classPrediction;
  }
}

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

  const contentElement = document.createElement("span");
  contentElement.innerText = comment.content;

  const deleteButtonElement = document.createElement("button");
  deleteButtonElement.innerText = "Delete";
  deleteButtonElement.addEventListener("click", () => {
    deleteComment(comment);

    commentElement.remove();
  });

  commentElement.appendChild(contentElement);
  commentElement.appendChild(deleteButtonElement);
  return commentElement;
}

function deleteComment(comment) {
  const params = new URLSearchParams();
  params.append("id", comment.id);
  fetch("/delete-comment", {method: "POST", body: params});
}

function loadMap() {
  const sanFrancisco = {lat: 37.7749, lng: -122.4194};
  cafeMap = new google.maps.Map(
    document.getElementById("map"),
    {center: sanFrancisco, zoom: 12});

  cafeMap.addListener("click", (event) => {
    createMapMarkerForEdit(event.latLng.lat(), event.latLng.lng());
  });

  fetchMapMarkers();
  loadHardcodedMapItems();
}

async function fetchMapMarkers() {
  const response = await fetch("/mapmarkers");
  const mapMarkers = await response.json();
  mapMarkers.forEach((mapMarker) => {
    loadMapItem(mapMarker.itemName, mapMarker.latitude, mapMarker.longitude, mapMarker.content)
  });
}

function loadMapItem(itemName, latitude, longitude, itemDescription) {
  const itemMarker = new google.maps.Marker({
    position: {lat: latitude, lng: longitude},
    map: cafeMap,
    title: itemName
  });

  const itemInfoWindow = new google.maps.InfoWindow({
    content: "<h1>"+itemName+"</h1>"+"<p>"+itemDescription+"</p>"
  });

  itemMarker.addListener("click", function() {
    itemInfoWindow.open(cafeMap, itemMarker);
  });
}

function loadHardcodedMapItems(){
  loadMapItem("Philz Coffee", 37.793949, -122.398062, "I like the Iced Coffee Rose!");
  loadMapItem("Saint Frank Coffee", 37.779511, -122.410411, 
              "The hot chocolate & cappuccino are superb");
  loadMapItem("Stonemill Matcha", 37.764730, -122.421731, "I like the yuzu meringue");
  loadMapItem("Four Barrel Coffee", 37.768055, -122.422117, "The lattes are spectacular!");
}

function postMapMarker(itemName, latitude, longitude, content) {
  const params = new URLSearchParams();
  params.append("itemName", itemName);
  params.append("latitude", latitude);
  params.append("longitude", longitude);
  params.append("content", content);

  fetch("/mapmarkers", {method:"POST", body:params});
}

function createMapMarkerForEdit(latitude, longitude){
  // remove editable marker if one is already shown
  if (editMarker) {
    editMarker.setMap(null);
  }

  editMarker = new google.maps.Marker({position: {lat: latitude, lng: longitude}, map: cafeMap});

  const infoWindow = new google.maps.InfoWindow({content: buildInfoWindowInput(latitude, longitude)});

  google.maps.event.addListener(infoWindow, "closeclick", () => {
      editMarker.setMap(null);
  });

  infoWindow.open(cafeMap, editMarker);
}

function buildInfoWindowInput(latitude, longitude) {
  const titleBox = document.createElement("textarea");
  const descriptionBox = document.createElement("textarea");
  const submitButton = document.createElement("button");
  submitButton.appendChild(document.createTextNode("Submit"));

  submitButton.onclick = () => {
    postMapMarker(titleBox.value, latitude, longitude, descriptionBox.value);
    loadMapItem(titleBox.value, latitude, longitude, descriptionBox.value);
    editMarker.setMap(null);
  };

  const entryBox = document.createElement("div");
  entryBox.appendChild(titleBox);
  entryBox.appendChild(document.createElement("br"));
  entryBox.appendChild(descriptionBox);
  entryBox.appendChild(document.createElement("br"));
  entryBox.appendChild(submitButton);

  return entryBox;
}

async function displayElement() {
  const loginStatusMessage = document.getElementById("loginStatusMessage");
  const commentSection = document.getElementById("commentSection");
  
  userLoginInfo = await fetchLoginStatus();
 
  if (userLoginInfo.isLoggedIn === true) {
    commentSection.style.visibility = "visible";
    loginStatusMessage.innerHTML = "<a href=\"" + userLoginInfo.redirectUrl + "\">log out here</a>";
  } else {
    commentSection.style.visibility = "hidden";
    loginStatusMessage.innerHTML = "<p>you are not logged in. <a href=\"" + userLoginInfo.redirectUrl + "\">log in here to comment!</a></p>";
  }
}

async function fetchLoginStatus() {
  const response = await fetch("/authentication");
  return await response.json();
}

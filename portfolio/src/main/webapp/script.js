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

  imageContainer.innerHTML = '';
  imageContainer.appendChild(imgElement);
}

async function getGreeting() {
  console.log('Fetching a greeting.');
 
  const response = await fetch('/data');
  const greeting = await response.text();
  document.getElementById('greeting').innerHTML = greeting;
}
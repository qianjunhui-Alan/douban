let loadmore = false;
const hiddeninfo = document.querySelectorAll(".hidden");
const viewAllSongs = document.querySelector(".view");
viewAllSongs.addEventListener('click' , function () {
    loadmore = !loadmore;
    for(let i = 0 ; i < hiddeninfo.length;i++ ){
        if(loadmore){
            hiddeninfo[i].style.display = 'block';
        }else{
            hiddeninfo[i].style.display = 'none';
        }
    }
})


let islike = false;
const likebutton = document.querySelector("body > div:nth-child(4) > div > div.info > button");
const likevalue = document.querySelector("body > div:nth-child(4) > div > div.info > button > span");
likebutton.addEventListener("click" , function (){
    islike = ! islike;
    if(islike){
        likebutton.classList.add("has-like");
        likevalue.innerHTML = parseInt(likevalue.innerHTML.trim()) + 1 ;
    }else{
        likebutton.classList.remove("has-like");
        likevalue.innerHTML = parseInt(likevalue.innerHTML.trim()) - 1 ;
    }
})

let isshare = false;
const sharelink = document.querySelector(".share");
const twocode = document.querySelector(".twocode");
sharelink.addEventListener("click",function (){
    isshare = ! isshare
    if(isshare){
        twocode.style.display = "block";
    }else{
        twocode.style.display = "none";
    }
})
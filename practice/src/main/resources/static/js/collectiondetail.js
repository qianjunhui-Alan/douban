const othercollections = document.querySelector(".othercollections");
const songList = document.querySelector(".songList");
const songs = document.querySelector(".songs");
const othercollectionsList = document.querySelector(".othercollectionsList");
othercollections.addEventListener("click" , ()=>{
    othercollectionsList.style.display = 'block';
    othercollections.style.opacity = '1.0';
    songs.style.opacity = '0.4';
    songList.style.display = 'none';
})

songs.addEventListener("click" , ()=>{
    songList.style.display = 'block';
    songs.style.opacity = '1.0';
    othercollections.style.opacity = '0.4';
    othercollectionsList.style.display = 'none';

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
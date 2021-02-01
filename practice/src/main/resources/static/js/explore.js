let loadmore = false;
const moreinfo = document.querySelectorAll(".none");
const morebutton = document.querySelector(".loadmore");
morebutton.addEventListener('click' , function () {
    loadmore = !loadmore;
    for(let i = 0 ; i < moreinfo.length;i++ ){
        if(loadmore){
            moreinfo[i].style.display = 'block';
        }else{
            moreinfo[i].style.display = 'none';
        }
    }
})




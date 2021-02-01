const input = document.querySelector("input");
input.addEventListener("input",(e)=>{
    console.log(e);
    fetch("http://127.0.0.1:8080/searchContent?keyword="+e.data)
        .then(function (response){
            return response.json();
        }).then(function (myjson){

            function createItem(item){
                const li = document.createElement("li");
                li.innerHTML = `<img src=${item.cover} referrerPolicy="no-referrer"> 
                                <div class="name">${item.name}</div>`
                return li;
            }

            const ul = document.querySelector("ul");
            ul.innerHTML = ''
            if(myjson.songs != null){
                for(let i = 0; i < myjson.songs.length;i++){
                    const li = createItem(myjson.songs[i]);
                    ul.appendChild(li);
                }
            }else{
                ul.innerHTML = '';
            }
    })

})
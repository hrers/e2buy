const shortcut = {
    template: "\
    <div class='py-container'> \
        <div class='shortcut'> \
            <ul class='fl'> \
               <li class='f-item'>易趣买欢迎您！</li> \
               <li class='f-item' v-if='user && user.username'>\
               尊敬的会员，<span style='color: red;'>{{user.username}}<!--<a href='javascript:void(0)' @click='logout'>  退出登录 </a>--></span>\
               </li>\
               <li v-else class='f-item'> \
                   请<a href='javascript:void(0)' @click='gotoLogin'>登录</a>　 \
                   <span><a href='register.html' target='_blank'>免费注册</a></span> \
               </li> \
           </ul> \
            <ul class='fr'> \
               <li class='f-item'></li> \
               <li class='f-item space'></li> \
               <li class='f-item'><a href='home.html' target='_blank'></a></li> \
               <li class='f-item space'></li> \
               <li class='f-item'></li> \
               <li class='f-item space'></li> \
               <li class='f-item'></li> \
               <li class='f-item space'></li> \
               <li class='f-item'></li> \
               <li class='f-item space'></li> \
               <li class='f-item' id='service'> \
                   <span></span> \
                   <ul class='service'> \
                       <li><a href='cooperation.html' target='_blank'></a></li> \
                       <li><a href='shoplogin.html' target='_blank'></a></li> \
                       <li><a href='cooperation.html' target='_blank'></a></li> \
                       <li><a href='#'></a></li> \
                   </ul> \
               </li> \
               <li class='f-item space'></li> \
               <li class='f-item'></li> \
           </ul> \
       </div> \
    </div>\
    ",
    name: "shortcut",
    data() {
        return {
            user: null
        }
    },
    created() {
        e2b.http("/auth/verify")
            .then(resp => {
                this.user = resp.data;
            })
    },
    methods: {
        gotoLogin() {
            window.location = "login.html?returnUrl=" + window.location;
        },
        logout() {
            localStorage.clear();
            window.location = 'http://www.e2buy.com'
        }
    }
}
export default shortcut;
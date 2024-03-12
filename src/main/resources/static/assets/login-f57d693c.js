import{a as p}from"./apiService-77935b08.js";import{c as h}from"./urls-04ad3523.js";import{A as r}from"./auth-eef4144a.js";import{s as v,w as b}from"./realTimeEdu-31352dc5.js";import{_ as k,m as S,r as c,o as w,c as y,e as n,w as _,i as I,a as o,n as x,p as P,g as V}from"./index-e96a63ea.js";import"./axios-c24e582b.js";const C={computed:{...S(b,["loginSocketData"])},watch:{loginSocketData(e){let s={};switch(e.type){case"loginCheck":e.result=="success"?this.loginSuccess():(s={type:"confirm",message:"This ID is already logged in.<br>Would you like to cancel the existing login and log in again?",doAction:()=>{v.reLogin(this.uid)},cancleAction:()=>{this.emitter.emit("overlay","hide"),this.$router.back()}},this.emitter.emit("modalOpen",s));break;case"reLogin":e.result=="success"?this.loginSuccess():(this.emitter.emit("overlay","hide"),s={message:"현재 수업중인 아이디 입니다<br> 새로 로그인은 불가능합니다",doAction:()=>{this.$router.back()}},this.emitter.emit("modalOpen",s));break}}},data(){return{uid:"",upass:"",insertFlag:!1,form:null,loginResult:{},active:""}},created(){const e=r.getSession();if(e&&(e!=null&&e.memberInfo)){this.$router.push("/");return}},methods:{inputCheck(){this.uid&&this.upass?this.active="active":this.active=""},loginSuccess(){const e={uid:this.uid,upass:this.upass,type:"success"};p.post(h,e,s=>{this.emitter.emit("overlay","hide"),s.status=="success"?(r.setSession(s),this.loginCheckParams()):this.emitter.emit("modalOpen",{message:"잘못된 접근입니다"})})},loginCheckParams(){let e="/";sessionStorage.getItem("shareLoginUrl")&&(e=sessionStorage.getItem("shareLoginUrl"),sessionStorage.setItem("shareLoginUrl","")),location.href=e},validate(){if(this.form){const e={uid:this.uid,upass:this.upass,type:"success"};if(this.insertFlag)return;this.insertFlag=!0,this.emitter.emit("overlay","open"),p.post(h,e,s=>{if(this.insertFlag=!1,this.emitter.emit("overlay","hide"),s.status=="success")r.setSession(s),this.loginCheckParams();else{this.emitter.emit("overlay","hide");let m=s.code,t="";switch(m){case"notauth":t="You are an unverified member";break;case"uout":t="I am a member who has withdrawn";break;default:t="Invalid ID or password";break}this.insertFlag=!1;const a={message:t};this.emitter.emit("modalOpen",a)}})}},findPass(){this.$router.push("/memberFindPass")}}},l=e=>(P("data-v-75f08cd0"),e=e(),V(),e),L={class:"login-container"},U=l(()=>o("p",{class:"page-title"},"Log in with email",-1)),A={class:"input-box"},F=l(()=>o("div",{class:"label"},"Email",-1)),O={class:"input-box"},B=l(()=>o("div",{class:"label"}," Password",-1)),D=l(()=>o("div",{class:"find-box"},null,-1)),N={class:"button-row"},E=l(()=>o("span",null,"Log in",-1));function R(e,s,u,m,t,a){const d=c("v-text-field"),f=c("v-btn"),g=c("v-form");return w(),y("div",L,[U,n(g,{modelValue:t.form,"onUpdate:modelValue":s[2]||(s[2]=i=>t.form=i),novalidate:"",class:"w-full",onSubmit:I(a.validate,["prevent"])},{default:_(()=>[o("div",A,[F,n(d,{modelValue:t.uid,"onUpdate:modelValue":s[0]||(s[0]=i=>t.uid=i),rules:[i=>!!i||"Please enter your email"],variant:"outlined",onInput:a.inputCheck,placeholder:"Please enter your email"},null,8,["modelValue","rules","onInput"])]),o("div",O,[B,D,n(d,{type:"password",autocomplete:"on",variant:"outlined",onInput:a.inputCheck,rules:[i=>!!i||"Please enter a password"],modelValue:t.upass,"onUpdate:modelValue":s[1]||(s[1]=i=>t.upass=i),placeholder:"Please enter a password"},null,8,["onInput","rules","modelValue"])]),o("div",N,[n(f,{type:"submit",class:x("submitButton "+t.active),variant:"tonal"},{default:_(()=>[E]),_:1},8,["class"])])]),_:1},8,["modelValue","onSubmit"])])}const Y=k(C,[["render",R],["__scopeId","data-v-75f08cd0"]]);export{Y as default};
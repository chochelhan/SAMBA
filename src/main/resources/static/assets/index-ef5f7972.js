import{A as t}from"./auth-eef4144a.js";import{_ as r,r as s,o as n,j as c}from"./index-e96a63ea.js";import"./urls-04ad3523.js";const a={created(){const e=t.getSession();if(e&&(e!=null&&e.memberInfo))switch(this.role=e.memberInfo.role,this.role){case"ROLE_TEACHER":case"ROLE_MANAGER":location.href="/";break}else location.href="/"}};function i(e,_,f,p,l,m){const o=s("RouterView");return n(),c(o)}const R=r(a,[["render",i]]);export{R as default};
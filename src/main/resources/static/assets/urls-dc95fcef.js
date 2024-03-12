import{a as A}from"./axios-c24e582b.js";const n={Authorization:"","Content-Type":"application/json;charset=UTF-8"},i={Authorization:"","Content-Type":"multipart/form-data;charset=utf-8;"};A.defaults.withCredentials=!0;const E={DEBUG:!1,axios:A,authToken:sessionStorage.getItem("authToken"),adminInfo:sessionStorage.getItem("adminInfo"),setAdminInfo(){this.authToken?(n.Authorization="Bearer "+this.authToken,i.Authorization="Bearer "+this.authToken):(n.Authorization="",i.Authorization="")},post(e,o,_,s){this.checkDebug(),A.post(e,o,{headers:n}).then(a=>{switch(this.DEBUG&&console.log(a.data),a.data.status){case"success":case"message":_(a.data);break;case"error":alert("결과값이 없습니다"),this.goAdminHome();break;case"fail":alert("잘못된 접근입니다"),this.goAdminHome();break}}).catch(a=>{this.setError(a)})},postFile(e,o,_){this.checkDebug(),A.post(e,o,{headers:i}).then(s=>{switch(this.DEBUG&&console.log(s.data),s.data.status){case"success":case"message":_(s.data);break;case"error":alert("결과값이 없습니다"),this.goAdminHome();break;case"fail":alert("잘못된 접근입니다"),this.goAdminHome();break}}).catch(s=>{this.setError(s)})},get(e,o,_,s){this.checkDebug(),A.get(e,{params:o,headers:n}).then(a=>{if(this.DEBUG&&console.log(a.data),a.data.status=="notLogin"){location.href="/";return}switch(a.data.status){case"success":case"message":_(a.data);break;case"error":alert("결과값이 없습니다"),this.goAdminHome();break;case"fail":alert("잘못된 접근입니다"),this.goAdminHome();break}}).catch(a=>{s?s(a.response.data):this.setError(a)})},getFileDownload(e,o,_){this.checkDebug(),A({url:e,method:"GET",params:o,headers:n,responseType:"blob"}).then(s=>{_(s.data)}).catch(s=>{this.setError(s)})},goAdminHome(){sessionStorage.setItem("authToken",""),sessionStorage.setItem("adminInfo",""),this.setAdminInfo(),location.href="/admin"},checkDebug(){this.DEBUG=!(location.hostname.indexOf("aimentor")>=0)},setError(e){this.DEBUG?(console.log(e),e.response?(console.log(e.response.data),console.log(e.response.status),console.log(e.response.headers)):e.request?console.log(e.request):console.log("Error",e.message)):this.goAdminHome()}},t="/api/admin/controller/",c="/admin/controller/checkStatus",T="/admin/controller/login",r=t+"member/logout",D=t+"member/updateAdminInfo",P=t+"member/getAdminInfo",d=t+"member/getAdminMainInfo",M=t+"setting/updateSettingSns",g=t+"setting/getSettingSns",m=t+"setting/updateSettingAgree",l=t+"setting/getSettingAgree",h=t+"setting/getSettingEmail",N=t+"setting/updateSettingEmail",u=t+"setting/getSettingCategory",S=t+"setting/updateSettingCategory",G=t+"member/getMemberList",L=t+"member/updateMemberInfo",p=t+"member/deleteMember",b=t+"member/getTeacherListBySchoolId",B=t+"member/getStudentListByGclass",U=t+"member/updateMemberDirectPayment",R=t+"boardArticle/getBoardArticleList",O=t+"boardArticle/updateBoardArticleAnswer",f=t+"boardArticle/deleteBoardArticle",C=t+"statistics/getPayment",k=t+"statistics/getMember",H=t+"statistics/getMemberWithSchool",y=t+"edu/insertEduProblem",F=t+"edu/updateEduProblem",Y=t+"edu/deleteEduProblem",x=t+"edu/getEduProblemList",w=t+"edu/getEduProblem",z=t+"edu/updateEduProhibit",v=t+"edu/getEduProhibit",K=t+"recover/getBackupData",W=t+"recover/recoverBackupData",q="/api/admin/recover/downloadBackupData",j=t+"setting/getSettingEduGtype",X=t+"setting/updateSettingEduGtype",J=t+"payment/getPaymentList",Q=t+"payment/updatePaymentComplete",V=t+"tax/getTaxList",Z=t+"tax/updateTaxPublish",$=t+"setting/getSchoolList",tt=t+"setting/deleteSchool",et=t+"setting/insertSchool",st=t+"setting/updateSchool";export{r as A,w as B,y as C,F as D,v as E,z as F,R as G,f as H,O as I,Q as J,J as K,b as L,B as M,H as N,k as O,C as P,Z as Q,V as R,W as S,K as T,q as U,E as a,T as b,c,d,P as e,D as f,L as g,U as h,G as i,p as j,$ as k,tt as l,et as m,st as n,u as o,S as p,j as q,X as r,g as s,M as t,l as u,m as v,h as w,N as x,x as y,Y as z};
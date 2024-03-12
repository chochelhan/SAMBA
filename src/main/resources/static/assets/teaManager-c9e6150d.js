import{a as y}from"./apiService-77935b08.js";import{k as x,j as B}from"./urls-04ad3523.js";import{A as U}from"./auth-eef4144a.js";import{U as f}from"./util-75dcb7cf.js";import{_ as S,r as D,o as c,c as u,a as s,h as _,t as n,F as m,b as A,n as d,d as p,j as T,w as g,p as k,g as E}from"./index-e96a63ea.js";import"./axios-c24e582b.js";const L={data(){return{userList:[],serverFlag:!1,payUseType:"",pcView:!0,totalCount:0,authTotal:0}},created(){const o=U.getSession();if(!o||o.memberInfo.role!="ROLE_MANAGER"){location.href="/";return}window.document.body.clientWidth<=1023&&(this.pcView=!1),this.getData()},methods:{getData(){this.userList=[],y.post(x,{},o=>{this.payUseType=o.payUseType;let h=0;const a=new Date(f.getStandardNowDate()),i=a.getMonth()+1>9?a.getMonth()+1:"0"+(a.getMonth()+1),l=a.getDate()>9?a.getDate():"0"+a.getDate(),r=a.getFullYear().toString()+i+l;for(let t of o.teachers){if(t.payUseType=="teacher")if(t.payEndDate){let e=t.payEndDate.replace("-","").replace("-","");parseInt(r)<parseInt(e)?(t.updButton=!1,t.schoolAuthText="선생님 요금제"):(t.updButton=!0,t.schoolAuth="no",t.schoolAuthText="미승인",t.schoolAuthButton="승인처리")}else t.updButton=!1,t.schoolAuthText="선생님 요금제";else t.payAuthStatus=="none"?(t.updButton=!0,t.schoolAuth="no",t.schoolAuthText="미승인",t.schoolAuthButton="승인처리"):t.payAuthStatus=="school"?(t.schoolAuth="yes",t.schoolAuthText="승인",t.schoolAuthButton="승인해제",t.updButton=!0,h++):(t.updButton=!1,t.schoolAuthText="선생님 요금제");this.userList.push(t)}this.totalCount=o.teachers.length,this.authTotal=h})},authTeacher(o){if(this.payUseType!="school"){const a={message:"학교 요금제일 경우에만 사용가능합니다"};this.emitter.emit("modalOpen",a);return}const h={type:"confirm",message:"승인상태를 변경하시겠습니까?",doAction:()=>{this.authTeacherActive(o)}};this.emitter.emit("modalOpen",h)},authTeacherActive(o){const h={id:o.id.toString(),sendType:"teacher",payAuthStatus:o.payAuthStatus=="none"?"school":"none"};this.emitter.emit("overlay","open"),y.post(B,h,a=>{this.emitter.emit("overlay","hide");let i="승인상태를 변경하였습니다";a.status=="success"?this.getData():a.code=="schoolOver"?i="학교요금제 총 추가인원 ("+f.numberFormat(a.baseLimit)+")을 초과하였습니다":i="선생님 요금제를 사용중으로 변경이 불가능합니다";const l={message:i};this.emitter.emit("modalOpen",l)})}},mounted(){window.addEventListener("resize",()=>{window.document.body.clientWidth<=1023?this.pcView=!1:this.pcView=!0})},beforeUnmount(){window.removeEventListener("resize",()=>{window.document.body.clientWidth<=1023?this.pcView=!1:this.pcView=!0})}},v=o=>(k("data-v-ed156ff8"),o=o(),E(),o),C={class:"edu-container"},I=v(()=>s("div",{class:"page-title"},"선생님 명단",-1)),V={class:"page-user-info"},M={key:0,class:"edu-table",cellspacing:"0",cellpadding:"0",border:"0"},O=v(()=>s("thead",null,[s("tr",null,[s("th",{style:{width:"200px"}},"학년"),s("th",{style:{width:"200px"}},"반"),s("th",{style:{width:"200px"}},"이름"),s("th",null,"상태"),s("th",{style:{width:"200px"}},"승인")])],-1)),R={key:1,class:"edu-ul"},F={class:"subject-box"},N={class:"subject"},j={class:"date"},H={class:"button-box"};function P(o,h,a,i,l,r){const t=D("v-btn");return c(),u("div",C,[I,s("div",V,[_(" 총 선생님수 "),s("span",null,n(l.totalCount),1),_("명 / 승인 수 "),s("span",null,n(l.authTotal),1),_("명 ")]),l.pcView?(c(),u("table",M,[O,s("tbody",null,[(c(!0),u(m,null,A(l.userList,(e,w)=>(c(),u("tr",null,[s("td",null,n(e.grade)+"학년",1),s("td",null,n(e.gclass)+"반",1),s("td",null,n(e.name)+" ("+n(e.uid)+")",1),s("td",null,[l.payUseType=="school"?(c(),u("div",{key:0,class:d(e.schoolAuth)},n(e.schoolAuthText),3)):p("",!0)]),s("td",null,[e.updButton&&l.payUseType=="school"?(c(),T(t,{key:0,variant:"outlined",class:d("btn_"+e.schoolAuth),onClick:b=>r.authTeacher(e)},{default:g(()=>[s("span",null,n(e.schoolAuthButton),1)]),_:2},1032,["class","onClick"])):p("",!0)])]))),256))])])):(c(),u("ul",R,[(c(!0),u(m,null,A(l.userList,(e,w)=>(c(),u("li",null,[s("div",F,[s("div",N,n(e.name)+" ("+n(e.uid)+") ",1),s("div",j,n(e.grade)+"학년 "+n(e.gclass)+"반 ",1)]),s("div",H,[l.payUseType=="school"?(c(),u("div",{key:0,class:d(e.schoolAuth)},n(e.schoolAuthText),3)):p("",!0),e.updButton&&l.payUseType=="school"?(c(),T(t,{key:1,variant:"outlined",class:d("btn_"+e.schoolAuth),onClick:b=>r.authTeacher(e)},{default:g(()=>[s("span",null,n(e.schoolAuthButton),1)]),_:2},1032,["class","onClick"])):p("",!0)])]))),256))]))])}const K=S(L,[["render",P],["__scopeId","data-v-ed156ff8"]]);export{K as default};
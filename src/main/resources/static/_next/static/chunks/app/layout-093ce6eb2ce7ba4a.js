(self.webpackChunk_N_E=self.webpackChunk_N_E||[]).push([[177],{3964:(e,t,n)=>{Promise.resolve().then(n.bind(n,5614)),Promise.resolve().then(n.t.bind(n,9840,23)),Promise.resolve().then(n.t.bind(n,9324,23)),Promise.resolve().then(n.bind(n,814))},5614:(e,t,n)=>{"use strict";n.d(t,{ThemeProvider:()=>v});var r=n(5155),l=n(2115);let a=["light","dark"],s="(prefers-color-scheme: dark)",o="undefined"==typeof window,i=(0,l.createContext)(void 0),m=e=>(0,l.useContext)(i)?l.createElement(l.Fragment,null,e.children):l.createElement(d,e),c=["light","dark"],d=({forcedTheme:e,disableTransitionOnChange:t=!1,enableSystem:n=!0,enableColorScheme:r=!0,storageKey:o="theme",themes:m=c,defaultTheme:d=n?"system":"light",attribute:v="data-theme",value:$,children:b,nonce:g})=>{let[S,p]=(0,l.useState)(()=>h(o,d)),[w,k]=(0,l.useState)(()=>h(o)),E=$?Object.values($):m,C=(0,l.useCallback)(e=>{let l=e;if(!l)return;"system"===e&&n&&(l=y());let s=$?$[l]:l,o=t?f():null,i=document.documentElement;if("class"===v?(i.classList.remove(...E),s&&i.classList.add(s)):s?i.setAttribute(v,s):i.removeAttribute(v),r){let e=a.includes(d)?d:null,t=a.includes(l)?l:e;i.style.colorScheme=t}null==o||o()},[]),T=(0,l.useCallback)(e=>{p(e);try{localStorage.setItem(o,e)}catch(e){}},[e]),_=(0,l.useCallback)(t=>{k(y(t)),"system"===S&&n&&!e&&C("system")},[S,e]);(0,l.useEffect)(()=>{let e=window.matchMedia(s);return e.addListener(_),_(e),()=>e.removeListener(_)},[_]),(0,l.useEffect)(()=>{let e=e=>{e.key===o&&T(e.newValue||d)};return window.addEventListener("storage",e),()=>window.removeEventListener("storage",e)},[T]),(0,l.useEffect)(()=>{C(null!=e?e:S)},[e,S]);let x=(0,l.useMemo)(()=>({theme:S,setTheme:T,forcedTheme:e,resolvedTheme:"system"===S?w:S,themes:n?[...m,"system"]:m,systemTheme:n?w:void 0}),[S,T,e,w,n,m]);return l.createElement(i.Provider,{value:x},l.createElement(u,{forcedTheme:e,disableTransitionOnChange:t,enableSystem:n,enableColorScheme:r,storageKey:o,themes:m,defaultTheme:d,attribute:v,value:$,children:b,attrs:E,nonce:g}),b)},u=(0,l.memo)(({forcedTheme:e,storageKey:t,attribute:n,enableSystem:r,enableColorScheme:o,defaultTheme:i,value:m,attrs:c,nonce:d})=>{let u="system"===i,h="class"===n?`var d=document.documentElement,c=d.classList;c.remove(${c.map(e=>`'${e}'`).join(",")});`:`var d=document.documentElement,n='${n}',s='setAttribute';`,f=o?a.includes(i)&&i?`if(e==='light'||e==='dark'||!e)d.style.colorScheme=e||'${i}'`:"if(e==='light'||e==='dark')d.style.colorScheme=e":"",y=(e,t=!1,r=!0)=>{let l=m?m[e]:e,s=t?e+"|| ''":`'${l}'`,i="";return o&&r&&!t&&a.includes(e)&&(i+=`d.style.colorScheme = '${e}';`),"class"===n?i+=t||l?`c.add(${s})`:"null":l&&(i+=`d[s](n,${s})`),i},v=e?`!function(){${h}${y(e)}}()`:r?`!function(){try{${h}var e=localStorage.getItem('${t}');if('system'===e||(!e&&${u})){var t='${s}',m=window.matchMedia(t);if(m.media!==t||m.matches){${y("dark")}}else{${y("light")}}}else if(e){${m?`var x=${JSON.stringify(m)};`:""}${y(m?"x[e]":"e",!0)}}${u?"":"else{"+y(i,!1,!1)+"}"}${f}}catch(e){}}()`:`!function(){try{${h}var e=localStorage.getItem('${t}');if(e){${m?`var x=${JSON.stringify(m)};`:""}${y(m?"x[e]":"e",!0)}}else{${y(i,!1,!1)};}${f}}catch(t){}}();`;return l.createElement("script",{nonce:d,dangerouslySetInnerHTML:{__html:v}})},()=>!0),h=(e,t)=>{let n;if(!o){try{n=localStorage.getItem(e)||void 0}catch(e){}return n||t}},f=()=>{let e=document.createElement("style");return e.appendChild(document.createTextNode("*{-webkit-transition:none!important;-moz-transition:none!important;-o-transition:none!important;-ms-transition:none!important;transition:none!important}")),document.head.appendChild(e),()=>{window.getComputedStyle(document.body),setTimeout(()=>{document.head.removeChild(e)},1)}},y=e=>(e||(e=window.matchMedia(s)),e.matches?"dark":"light");function v(e){let{children:t,...n}=e;return(0,r.jsx)(m,{...n,children:t})}},9324:()=>{},9840:e=>{e.exports={style:{fontFamily:"'Inter', 'Inter Fallback'",fontStyle:"normal"},className:"__className_d65c78"}}},e=>{var t=t=>e(e.s=t);e.O(0,[385,814,441,517,358],()=>t(3964)),_N_E=e.O()}]);
import { createApp } from 'vue'
import App from './App.vue'
import router from "./routers"
import "tailwindcss/tailwind.css"
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import pinia from './stores/createPinia';

const app = createApp(App);

for (const [key, component] of Object.entries(ElementPlusIconsVue)){
    app.component(key, component)
};

app.use(pinia);
app.use(router);
app.use(ElementPlus);

app.mount("#app");

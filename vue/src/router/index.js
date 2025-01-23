import Vue from 'vue'
import VueRouter from 'vue-router'
import store from "@/store";

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/404',
    name: '404',
    component: () => import('../views/404.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// Provide a method to reset the routes
export const resetRouter = () => {
  router.matcher = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
  })
}

// Note: Refreshing the page will reset the route
export const setRoutes = () => {
  const storeMenus = localStorage.getItem("menus");
  if (storeMenus) {

    // Get the current route object name array
    const currentRouteNames = router.getRoutes().map(v => v.name)
    if (!currentRouteNames.includes('Manage')) {
      // Assemble dynamic routes
      const manageRoute = { path: '/', name: 'Manage', component: () => import('../views/Manage.vue'), redirect: "/home", children: [
          { path: 'person', name: 'Personal Information', component: () => import('../views/Person.vue')},
          { path: 'password', name: 'Change Password', component: () => import('../views/Password.vue')}
        ] }
      const menus = JSON.parse(storeMenus)
      menus.forEach(item => {
        if (item.path) {  // Only set routes when path is not empty
          let itemMenu = { path: item.path.replace("/", ""), name: item.name, component: () => import('../views/' + item.pagePath + '.vue')}
          manageRoute.children.push(itemMenu)
        } else if(item.children.length) {
          item.children.forEach(item => {
            if (item.path) {
              let itemMenu = { path: item.path.replace("/", ""), name: item.name, component: () => import('../views/' + item.pagePath + '.vue')}
              manageRoute.children.push(itemMenu)
            }
          })
        }
      })
      // Dynamically add the route to the current router object
      router.addRoute(manageRoute)
    }

  }
}

// Re-apply routes when resetting
setRoutes()

router.beforeEach((to, from, next) => {
  localStorage.setItem("currentPathName", to.name)  // Set the current route name
  store.commit("setPath")

  // Case when route is not found
  if (!to.matched.length) {
    const storeMenus = localStorage.getItem("menus")
    if (storeMenus) {
      next("/404")
    } else {
      // Redirect to login page
      next("/login")
    }
  }
  // Allow other routes to proceed
  next()

})

export default router

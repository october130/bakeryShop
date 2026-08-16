# 🍰 烘焙蛋糕定制平台 — 接口文档

> 基础路径：`http://localhost:8082`
> 除 AI 模块外，所有接口前缀为 `/api`
> 统一响应格式：`Result<T>` = `{ code: 200, message: "xxx", data: T }`
> 需要登录的接口：请求头带 `Authorization: Bearer <token>`
> **价格单位：分（Integer），前端需 ÷100 显示为元**

---

## 一、用户模块 `/api/user`

### 1. 注册
```
POST /api/user/register
Body: { phone: "13800001111", password: "123456", nickname: "张三" }
Response: Result<LoginVO>
  data.token: String
  data.userInfo: { id, phone, nickname, avatar, role }
```

### 2. 登录
```
POST /api/user/login
Body: { phone: "13800001111", password: "123456" }
Response: Result<LoginVO>
  data.token: String
  data.userInfo: UserVO
```

### 3. 获取当前用户信息
```
GET /api/user/info
Headers: Authorization: Bearer <token>
Response: Result<UserVO>
  data: { id, phone, nickname, avatar, role, createTime }
```

---

## 二、烘焙店模块 `/api/bakery`

### 4. 获取分类列表
```
GET /api/bakery/categories
Response: Result<List<CategoryVO>>
  data[]: { id, name, icon }
```

### 5. 获取烘焙店列表（支持 LBS 距离排序）
```
GET /api/bakery/list?latitude=30.27&longitude=120.15&categoryId=1
Response: Result<List<BakeryVO>>
  data[]: { id, name, address, phone, image, description,
            latitude, longitude, avgPrice, status, distance }
```

### 6. 获取烘焙店详情
```
GET /api/bakery/detail/{id}
Response: Result<BakeryVO>
```

---

## 三、蛋糕模块 `/api/cake`

### 7. 获取蛋糕列表（按店铺）
```
GET /api/cake/list/{bakeryId}
Response: Result<List<CakeVO>>
  data[]: { id, bakeryId, categoryId, name, image, price,
            description, customizable, status, sold }
```

### 8. 获取蛋糕详情
```
GET /api/cake/detail/{cakeId}
Response: Result<CakeVO>
```

### 9. 按分类获取蛋糕
```
GET /api/cake/category/{categoryId}
Response: Result<List<CakeVO>>
```

---

## 四、购物车模块 `/api/cart`

### 10. 添加购物车
```
POST /api/cart/add  🔒需登录
Body: { cakeId: 1, amount: 2 }
Response: Result（添加成功）
注意：amount 是增量（increment），不是绝对值
```

### 11. 获取购物车列表
```
GET /api/cart/list  🔒需登录
Response: List<CartItemVO>  ⚠️ 注意：直接返回数组，没有 Result 包装！
  data[]: { cakeId, bakeryId, cakeName, price, image, amount }
```

### 12. 删除购物车商品
```
DELETE /api/cart/remove/{cakeId}  🔒需登录
Response: Result（删除成功）
```

### 13. 购物车结算
```
POST /api/cart/checkout  🔒需登录
Response: Result<CheckoutVO>
  data: {
    totalPrice: Integer（分）
    items: [{ cakeId, bakeryId, cakeName, price, image, amount }]
  }
```

---

## 五、订单模块 `/api/order`

### 14. 创建订单（从购物车结算）
```
POST /api/order/checkout  🔒需登录
Body: {
  address: "杭州市西湖区xxx",
  phone: "13800001111",
  remark: "生日快乐",
  deliverTime: "2026-07-30T18:00:00"
}
Response: Result<OrderVO>
  data: {
    id, orderNo, totalPrice, status, createTime, payTime,
    items: [{ cakeId, cakeName, price, amount, customInfo }]
  }
```

### 15. 订单列表
```
GET /api/order/list  🔒需登录
Response: Result<List<OrderVO>>
```

### 16. 订单详情
```
GET /api/order/{orderId}  🔒需登录
Response: Result<OrderVO>
```

### 17. 支付订单
```
PUT /api/order/pay/{orderId}  🔒需登录
Response: Result
```

### 18. 取消订单
```
PUT /api/order/cancel/{orderId}  🔒需登录
Response: Result
```

### 商家端接口（需要商家角色）

### 19. 商家接单
```
PUT /api/order/admin/{orderId}/accept
订单状态：1已支付 → 2制作中
```

### 20. 商家发货
```
PUT /api/order/admin/{orderId}/deliver
订单状态：2制作中 → 3配送中
```

### 21. 商家完成
```
PUT /api/order/admin/{orderId}/complete
订单状态：3配送中 → 4已完成
```

---

## 六、限时抢购模块 `/api/flashSale`

### 22. 获取秒杀详情
```
GET /api/flashSale/detail/{id}
Response: Result<FlashSaleVO>
  data: {
    id, cakeId, cakeName, flashPrice, originalPrice,
    stock, beginTime, endTime, status
  }
  status: 0-未开始 1-进行中 2-已结束
```

### 23. 参与秒杀
```
POST /api/flashSale/{id}/seckill  🔒需登录
Response: Result<String>（返回订单号或错误信息）
```

⚠️ **前端缺失**：目前没有秒杀 API 模块和页面，需要补充：
```js
export const flashSaleApi = {
  detail: (id) => api.get(`/flashSale/detail/${id}`),
  seckill: (id) => api.post(`/flashSale/${id}/seckill`)
}
```

---

## 七、评价模块 `/api/review`

### 24. 提交评价
```
POST /api/review/create  🔒需登录
Body: {
  orderId: 1,        // @NotNull
  cakeId: 1,         // 可选
  score: 5,          // @NotNull @Min(1) @Max(5)
  content: "很好吃！", // @NotBlank
  images: ""          // 图片URL，逗号分隔
}
Response: Result
```

### 25. 商家回复评价
```
PUT /api/review/reply/{reviewId}?replyContent=感谢支持
Response: Result
```

### 26. 店铺评价列表（分页）
```
GET /api/review/bakery/{bakeryId}?page=1&size=10
Response: Result<List<ReviewVO>>
  data[]: { id, userId, orderId, bakeryId, cakeId, score,
            content, images, replyContent, replyTime, createTime }
```

### 27. 蛋糕评价列表（分页）
```
GET /api/review/cake/{cakeId}?page=1&size=10
Response: Result<List<ReviewVO>>
```

### 28. 查看订单评价
```
GET /api/review/order/{orderId}
Response: Result<ReviewVO>
```

### 29. 隐藏评价（管理员）
```
PUT /api/review/hide/{reviewId}
Response: Result
```

---

## 八、AI 推荐模块 `/ai`

### 30. AI 蛋糕推荐
```
GET /ai/recommend?userMsg=推荐生日蛋糕&sessionId=default
⚠️ 注意：前缀是 /ai 不是 /api/ai
Response: Result<CakeRecommendation>
  data: {
    recommendations: [{ id, name, price, ... }],
    deliverMessage: "配送信息..."
  }
  ⚠️ reason 字段已加 @JsonIgnore，不会返回
```

---

## 📊 前端问题清单

| # | 问题 | 位置 | 说明 |
|---|------|------|------|
| 1 | 购物车列表不走 Result 包装 | `api/index.js` | 拦截器有数组判断 `if (Array.isArray(res)) return res`，已处理 |
| 2 | 购物车总价单位错误 | `stores/cart.js` | totalPrice 已改为 ÷100 ✅ |
| 3 | 秒杀模块缺失 | `api/modules.js` | 无 flashSaleApi，无秒杀页面 |
| 4 | 评价页面字段对齐 | `views/Review.vue` | 表单用 `score` 字段，与后端 ReviewDTO 一致 ✅ |
| 5 | 订单创建后购物车未清空 | `views/Cart.vue` | `clearCart()` 逐个删除，依赖 remove 接口 |
| 6 | 蛋糕列表 price 未格式化 | `views/CakeList.vue` | 已改为 ÷100 ✅ |
| 7 | 图片显示 emoji 占位 | `views/CakeDetail.vue` | 已改为 img 标签 + 默认图 ✅ |
| 8 | 缺少抢购页面 | 前端 | 需要新建 FlashSale.vue 页面 |
| 9 | AI 接口 baseURL 不同 | `api/modules.js` | AI 用独立 aiClient（baseURL: '/'），其他用 api（baseURL: '/api'） |
| 10 | 商家端页面缺失 | 前端 | 无接单/发货/完成的管理页面 |

---

## 🔑 订单状态流转

```
0-待支付 → 1-已支付 → 2-制作中 → 3-配送中 → 4-已完成
   ↓                                        ↑
   └──────────── 5-已取消 ──────────────────┘
```

- 0→5：用户取消 / 15分钟超时自动取消（RabbitMQ 延迟队列）
- 1→2：商家接单
- 2→3：商家发货
- 3→4：商家完成

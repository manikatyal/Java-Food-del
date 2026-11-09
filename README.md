# Food Delivery App

A full-stack food ordering app with three parts:

| Folder | What it is | Stack |
|---|---|---|
| `backend/` | REST API | Java 21, Spring Boot 3, Spring Data MongoDB, JWT, BCrypt, Stripe |
| `frontend/` | Customer site: menu, search, cart, checkout, my orders | React 18, Vite |
| `admin/` | Admin panel: add/remove dishes, manage order status | React 18, Vite |

```
 frontend (React) ──┐
                    ├──► backend (Spring Boot) ──► MongoDB
 admin (React) ─────┘            │
                                 └──► Stripe Checkout
```

## How an order works

1. User logs in. The backend returns a JWT; the React app sends it in a `token` header.
2. Adding to cart calls `/api/cart/add`. The cart is stored on the user in MongoDB and updated with an atomic `$inc`.
3. Checkout posts the cart to `/api/order/post`. The backend saves an unpaid order, clears the cart and returns a Stripe Checkout URL.
4. Stripe sends the user back to `/verify?success=true&orderId=...`. The frontend calls `/api/order/verify`, which marks the order paid (or deletes it if the payment was cancelled).
5. The admin panel lists all orders and moves them through `Food Processing → Out for Delivery → Delivered`.

## Run locally

You need Java 21, Maven, Node 18+, a MongoDB database (local or Atlas free tier) and a Stripe **test** key.

**1. Backend** (port 4000)

```bash
cd backend
export MONGO_URI="mongodb+srv://<user>:<pass>@<cluster>/food-delivery"
export JWT_SECRET="<any long random string>"
export STRIPE_SECRET_KEY="sk_test_..."
mvn spring-boot:run
```

On first start with an empty database it loads a starter menu of 32 dishes (`backend/src/main/resources/seed/foods.json`, photos in `backend/uploads/`). Set `SEED_MENU=false` to skip this.

**2. Frontend** (port 5173)

```bash
cd frontend
npm install
npm run dev
```

**3. Admin** (port 5174)

```bash
cd admin
npm install
npm run dev
```

Both React apps call `http://localhost:4000` by default. To point them somewhere else, create a `.env` file in that folder (see `.env.example`):

```
VITE_API_URL=https://your-backend.example.com
```

To pay in Stripe test mode use card `4242 4242 4242 4242`, any future expiry, any CVC.

## Backend config

| Variable | Required | Default | Meaning |
|---|---|---|---|
| `MONGO_URI` | yes | – | MongoDB connection string. The path part is the database name. |
| `JWT_SECRET` | yes | – | Key used to sign login tokens |
| `STRIPE_SECRET_KEY` | yes for checkout | – | Stripe secret key (`sk_test_...`) |
| `FRONTEND_URL` | no | `http://localhost:5173` | Where Stripe sends the user after payment |
| `UPLOAD_DIR` | no | `uploads` | Folder for dish photos |
| `SEED_MENU` | no | `true` | Load the starter menu into an empty database |
| `PORT` | no | `4000` | HTTP port |

## API

| Method | Path | Login | Body |
|---|---|---|---|
| GET | `/api/food/list` | – | – |
| POST | `/api/food/search` | – | `{"name": "salad"}` |
| POST | `/api/food/add` | – | multipart: `name`, `description`, `price`, `category`, `image` |
| POST | `/api/food/remove` | – | `{"id": "..."}` |
| GET | `/images/{file}` | – | – |
| POST | `/api/user/register` | – | `{"name", "email", "password"}` |
| POST | `/api/user/login` | – | `{"email", "password"}` |
| POST | `/api/cart/add` | yes | `{"itemId": "..."}` |
| POST | `/api/cart/remove` | yes | `{"itemId": "..."}` |
| POST | `/api/cart/get` | yes | `{}` |
| POST | `/api/order/post` | yes | `{"items": [...], "amount": 38, "address": {...}}` |
| POST | `/api/order/verify` | – | `{"orderId": "...", "success": "true"}` |
| POST | `/api/order/userorders` | yes | `{}` |
| GET | `/api/order/list` | – | – |
| POST | `/api/order/status` | – | `{"orderId": "...", "status": "Delivered"}` |

Every response is JSON shaped like `{"success": true, ...}` or `{"success": false, "message": "..."}`.

## Tests

```bash
cd backend && mvn test
```

## Deploy (Render)

- **Backend:** New Web Service → Runtime **Docker** → Root Directory `backend`. Add the env vars above, with `FRONTEND_URL` set to the frontend's URL.
- **Frontend / Admin:** New Static Site → Root Directory `frontend` (or `admin`) → Build `npm install && npm run build` → Publish `dist`. Add env var `VITE_API_URL` = backend URL. Add a rewrite rule `/*` → `/index.html` so page refreshes on `/cart` or `/orders` work.

## Known limits

- Admin endpoints have no login yet. Anyone who knows the URLs can add dishes or see orders.
- Uploaded photos are saved on the server's disk. On hosts with temporary disks (Render free tier) new uploads disappear on restart. The 32 starter photos are built into the Docker image, so they stay.
- Prices are in USD and converted to INR at a fixed x80 rate for Stripe.

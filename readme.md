# Coupons Management API - Monk Commerce

## Overview
This is a RESTful API for managing and applying different types of discount coupons for an e-commerce platform. The API supports three types of coupons:

- **Cart-wise**: Applies a discount to the entire cart if the total amount exceeds a threshold.
- **Product-wise**: Applies a discount to specific products.
- **Buy X Get Y (BxGy)**: Buy a certain number of products and get others for free, with a repetition limit.

The implementation is designed to be extendable, allowing new coupon types to be added in the future.

---

## API Endpoints

### **Coupon Management**
- **POST /coupons** - Create a new coupon.
- **GET /coupons** - Retrieve all coupons.
- **GET /coupons/{id}** - Retrieve a specific coupon by ID.
- **PUT /coupons/{id}** - Update a specific coupon by ID.
- **DELETE /coupons/{id}** - Delete a specific coupon by ID.

### **Applying Coupons**
- **POST /applicable-coupons** - Fetch all applicable coupons for a given cart.
- **POST /apply-coupon/{code}** - Apply a specific coupon to the cart and return the updated cart with discounted prices.

---

## Database Schema
Each coupon consists of the following fields:
- `id` (Unique identifier)
- `type` (cart-wise, product-wise, BxGy)
- `discount details` (thresholds, product IDs, percentages, etc.)
- `conditions for applicability`
- `expiration date` (if applicable)

---

## Example Coupon Cases
### **Cart-wise Coupons**
- 10% off on carts over Rs. 100
    - **Condition**: Cart total > 100
    - **Discount**: 10% off the entire cart

### **Product-wise Coupons**
- 20% off on Product A
    - **Condition**: Product A is in the cart
    - **Discount**: 20% off Product A

### **BxGy Coupons**
- Buy 2 from [X, Y, Z] and get 1 from [A, B, C] for free
    - **Condition**: At least 2 products from the buy list
    - **Repetition Limit**: Can be applied multiple times if the condition is met

---
#### **Cart-wise Coupons**
#### **Product-wise Coupons**
- These 2 coupons are pretty much straight forward. They apply a discount to the entire cart or specific products based on the conditions.

---
## BxGy Discount Calculation - Step-by-Step

## Step 1: Identify Buy Products
We check each cart item and see if its **ID** is in `buyProductIds = "1,2"`.

**Matching items:**
- **Product 1** → Quantity **3**
- **Product 2** → Quantity **1**

**Total Buy Items** = `3 + 1 = 4`

## Step 2: Check if Offer is Applicable
- The coupon requires at least `buyQuantity = 2` to activate.
- We have `totalBuyItems = 4`, so the offer **can** be applied.
- **Max times the offer applies** = `min(4 / 2, 2) = min(2, 2) = 2`.


## Step 3: Identify Free Products
We check cart items against `freeProductIds = "3,4"`.

**Matching items:**
- **Product 3** → Quantity **2**
- **Product 4** → Quantity **3**


## Step 4: Determine Free Items per Buy Product

### **For Product 1 (Bought 3 Units)**
- **Eligible free items** = `(3 / 2) * 1 = 1`
- **Limited by max repetitions**: `min(1, 2 * 1) = 1`
- We now distribute this `1` free item to available free products.

### **For Product 2 (Bought 1 Unit)**
- `(1 / 2) * 1 = 0` → **No free item earned**.


## Step 5: Apply Discount

### **Free Product 3 (Price = ₹200)**
- We apply `1` free item.
- **Discount** = `200 × 1 = ₹200`
- **Free product 3 quantity reduces** to `2 - 1 = 1`.


## Step 6: Update Cart Totals
- **Total Discount Applied** = `₹200`
- **Old Total** = Cart’s original total.
- **New Total** = `Old Total - Discount`

---

## Implemented Cases
- Cart-wise discount based on total value
- Product-wise discount for specific product IDs
- BxGy logic with quantity constraints and repetition limits

## Unimplemented Cases
- Coupon expiration handling
- Stacking of multiple coupons
- Advanced validation for conflicting coupons

## Assumptions
- A coupon can only be applied once per cart unless explicitly stated.
- Coupons are applied sequentially, not combined.
- BxGy coupons require the free products to be available in stock.

---

## Running the Project
1. Clone the repository.
2. Set up the database (H2, MySQL, or MongoDB).
3. Start the Spring Boot application.
4. Use Postman or an API testing tool to interact with the endpoints.

---

## Unit Tests
- Tests for checking applicable coupons
- Tests for applying different types of coupons
- Mocked repository and cart data for validation

---

## Future Improvements
- Add a coupon expiration feature
- Implement priority-based coupon application logic
- Optimize query performance for large datasets

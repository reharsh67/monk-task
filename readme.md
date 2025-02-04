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

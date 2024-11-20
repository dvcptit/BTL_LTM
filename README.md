# Project Structure - Game Phân Loại Rác

This project is divided into two main packages: `client` and `server`.

## Project Structure Overview

### Client
- **Controller**: Handles all user activities, such as button clicks, text input, etc.
- **CSS**: Manages all CSS for the project.
- **Model**: Manages all data for the project. Most models will resemble server models, but there may be some differences to suit client-side needs.

### Server
- **Controller**: Handles all server-side operations and communication with clients.
- **Model**: Manages server-side data structures, including the game's core logic.
- **Service**: Handles business logic, such as managing games, players, leaderboard, etc.

---

## Creating a New Page in the Client

When you want to create a new page for the client:

1. Go to `src/main/resources/com/n19/ltmproject` and create a new FXML file.
    - You can use ChatGPT to generate a new FXML file if needed.

2. Navigate to the `controller` package and create a new controller for the newly created FXML file.

3. Depending on your needs, you may also want to create or modify a model in the `model` package.

---

## Git Workflow for Branching and Pull Requests

In this project, pushing directly to the `main` branch is **restricted** to prevent issues with force pushes or unwanted changes. Always create a new branch and push your code to that branch, then create a pull request to merge changes into the `main` branch.

### Steps to Create a New Branch and Push Code

1. **Create a New Branch**:
   ```bash
   git checkout -b client-game-ui
   ```
   Replace `client-game-ui` with your branch name, e.g., `server-database-connection`.

2. **Make Changes**:
   Work on your feature or task within this branch.

3. **Stage and Commit Changes**:
   ```bash
   git add .
   git commit -m "Description of changes"
   ```

4. **Push to the Remote Branch**:
   ```bash
   git push origin client-game-ui
   ```
   Again, replace `client-game-ui` with your branch name.

5. **Create a Pull Request**:
    - Go to GitHub, navigate to repository.
    - You will see an option to create a pull request for your branch.
    - Submit the pull request to merge into the `main` branch.
    - Wait for repository owner.

**Important**: Do not push or force-push directly to `main`!

---

# Cấu Trúc Dự Án - Game Phân Loại Rác

Dự án này được chia thành hai gói chính: `client` và `server`.

## Tổng Quan Cấu Trúc Dự Án

### Client
- **Controller**: Xử lý tất cả các hoạt động của người dùng, như nhấp chuột, nhập văn bản, v.v.
- **CSS**: Quản lý tất cả các tệp CSS cho dự án.
- **Model**: Quản lý tất cả dữ liệu cho dự án. Hầu hết các model sẽ giống với model phía server, nhưng có thể sẽ có một số khác biệt để phù hợp với nhu cầu của phía client.

### Server
- **Controller**: Xử lý tất cả các hoạt động phía server và giao tiếp với client.
- **Model**: Quản lý các cấu trúc dữ liệu phía server, bao gồm logic cốt lõi của game.
- **Service**: Xử lý logic nghiệp vụ, như quản lý game, người chơi, bảng xếp hạng, v.v.

---

## Tạo Trang Mới Cho Client

Khi bạn muốn tạo một trang mới cho client:

1. Đi tới `src/main/resources/com/n19/ltmproject` và tạo một file FXML mới.
    - Bạn có thể sử dụng ChatGPT để tạo file FXML mới nếu cần.

2. Điều hướng đến gói `controller` và tạo một controller mới cho file FXML vừa tạo.

3. Tùy thuộc vào nhu cầu, bạn có thể muốn tạo hoặc chỉnh sửa một model trong gói `model`.

---

## Quy Trình Git Cho Nhánh và Pull Request

Trong dự án này, việc đẩy trực tiếp lên nhánh `main` bị **hạn chế** để tránh các vấn đề với việc ép đẩy (force push) hoặc thay đổi không mong muốn. Luôn luôn tạo một nhánh mới và đẩy code của bạn lên nhánh đó, sau đó tạo pull request để gộp thay đổi vào nhánh `main`.

### Các Bước Tạo Nhánh Mới và Đẩy Code

1. **Tạo Nhánh Mới**:
   ```bash
   git checkout -b client-game-ui
   ```
   Thay `client-game-ui` bằng tên nhánh của bạn, ví dụ: `server-database-connection`.

2. **Thực Hiện Thay Đổi**:
   Làm việc trên tính năng hoặc nhiệm vụ trong nhánh này.

3. **Stage và Commit Thay Đổi**:
   ```bash
   git add .
   git commit -m "Mô tả thay đổi"
   ```

4. **Đẩy Code Lên Nhánh Từ Xa**:
   ```bash
   git push origin client-game-ui
   ```
   Thay `client-game-ui` bằng tên nhánh của bạn.

5. **Tạo Pull Request**:
    - Vào GitHub, điều hướng đến repository.
    - Bạn sẽ thấy tùy chọn tạo pull request cho nhánh của bạn.
    - Gửi pull request để gộp vào nhánh `main`.
    - Đợi người quản lý repository duyệt.

**Lưu Ý**: Không đẩy hoặc ép đẩy trực tiếp vào nhánh `main`!

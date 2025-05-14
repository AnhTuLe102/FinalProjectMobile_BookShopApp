package com.bookstore.api.controller;

import com.bookstore.api.entity.cart.Cart;
import com.bookstore.api.entity.user.User;
import com.bookstore.api.response.UserResponse;
import com.bookstore.api.service.CartService;
import com.bookstore.api.service.RoleService;
import com.bookstore.api.service.UserService;
import com.bookstore.api.util.S3Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final CartService cartService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, CartService cartService, RoleService roleService) {
        this.userService = userService;
        this.cartService = cartService;
        this.roleService = roleService;
    }

    // expose "/users" and return a list of users
    @GetMapping("/users")
    public List<User> findAll() {
        return userService.findAll();
    }

    // add mapping for GET "/users/{userId}"
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> findById(@PathVariable int userId) {

        UserResponse response = userService.findById(userId);
        if (response.isError()) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // add mapping for POST "/users" - add new user
    @PostMapping("/users")
    public UserResponse addUser(@RequestBody User theUser) {

        // also just in case they pass an id in JSON ... set id to 0
        // this is to force a save of new item ... instead of update

        theUser.setId(0);

        User dbUser = userService.save(theUser);

        UserResponse userResponse = new UserResponse();
        userResponse.setError(false);
        userResponse.setMessage("Added user id - " + dbUser.getId());
        userResponse.setUser(dbUser);

        return userResponse;
    }

    // add mapping for PUT "/users" - update existing user
    @PutMapping("/users/{userId}")
    public ResponseEntity<UserResponse> updateCustomer(@PathVariable int userId,
            @RequestBody User user) {

        UserResponse response = userService.findById(userId);
        if (response.isError()) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        User theUser = response.getUser();

        theUser.setUserName(user.getUserName());
        theUser.setFullName(user.getFullName());
        theUser.setEmail(user.getEmail());
        theUser.setGender(user.getGender());
        theUser.setImages(user.getImages());
        theUser.setPassword(user.getPassword());
        theUser.setRole(user.getRole());

        User dbUser = userService.save(theUser);

        response.setError(false);
        response.setMessage("Updated user id - " + dbUser.getId());
        response.setUser(dbUser);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // add mapping for DELETE "/users/{userId}" - delete user
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<UserResponse> deleteCustomer(@PathVariable int userId) {

        UserResponse response = userService.findById(userId);
        if (response.isError()) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        userService.deleteById(userId);

        response.setError(false);
        response.setMessage("deleted user id - " + userId);
        response.setUser(null);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/users/login")
    public ResponseEntity<UserResponse> login(@RequestParam(required = false) String userName,
            @RequestParam(required = false) String password) {

        UserResponse response = userService.login(userName, password);
        if (response.isError()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/users/register")
    public ResponseEntity<UserResponse> register(@RequestBody User theUser) {
        UserResponse response = userService.checkInfo(theUser);
        if (response.isError()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        theUser.setId(0);
        theUser.setRole(roleService.findById(1));
        theUser.setImages(
                "https://user-images.githubusercontent.com/11250/39013954-f5091c3a-43e6-11e8-9cac-37cf8e8c8e4e.jpg");

        User dbUser = userService.save(theUser);
        Cart cart = new Cart();
        cart.setUser(dbUser);
        cartService.save(cart);

        response.setError(false);
        response.setMessage("Register successfully");
        response.setUser(dbUser);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/users/update-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> updateImages(
            @RequestPart("id") String userIdStr,
            @RequestPart("images") MultipartFile multipart) {

        int userId = Integer.parseInt(userIdStr);
        UserResponse response = userService.findById(userId);
        if (response.isError()) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            // Lấy tên gốc và đổi tên tránh trùng
            String originalFileName = multipart.getOriginalFilename();
            assert originalFileName != null;
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = "user_" + userId + "_" + System.currentTimeMillis() + extension;

            // Lưu vào thư mục static/uploads/
            Path uploadDir = Paths.get("uploads/");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path filePath = uploadDir.resolve(newFileName);
            Files.copy(multipart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Đường dẫn lưu vào DB (phía client dùng URL để hiển thị)
            String urlImage = "/uploads/" + newFileName;

            User user = response.getUser();
            user.setImages(urlImage);
            User dbUser = userService.save(user);

            response.setError(false);
            response.setMessage("Update images successfully");
            response.setUser(dbUser);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            response.setError(true);
            response.setMessage("Lỗi upload ảnh: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/users/update-password")
    public ResponseEntity<UserResponse> updatePassword(@RequestParam(required = false) int id,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String newPassword) {

        UserResponse response = userService.findById(id);
        if (response.isError()) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        User theUser = response.getUser();

        if (!theUser.getPassword().equals(password)) {
            response.setError(true);
            response.setMessage("Password does not match");
            response.setUser(null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        theUser.setPassword(newPassword);

        User dbUser = userService.save(theUser);

        response.setError(false);
        response.setMessage("Update password successfully");
        response.setUser(dbUser);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/users/update-info")
    public ResponseEntity<UserResponse> updateInfo(@RequestBody User theUser) {

        UserResponse response = userService.updateInfo(theUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users/roles/{roleId}")
    public List<User> getUserByRole(@PathVariable int roleId) {

        return userService.findUsersByRoleId(roleId);
    }

    @PostMapping("/users/add-employee")
    public ResponseEntity<UserResponse> addEmployee(@RequestBody User theUser) {

        UserResponse response = userService.checkInfo(theUser);
        if (response.isError()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        theUser.setId(0);
        theUser.setRole(roleService.findById(2));
        theUser.setImages("https://book-store-upload.s3.amazonaws.com/user-images/default-images.png");

        User dbUser = userService.save(theUser);

        response.setError(false);
        response.setMessage("Add employee successfully");
        response.setUser(dbUser);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

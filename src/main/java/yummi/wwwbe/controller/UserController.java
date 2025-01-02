package yummi.wwwbe.controller;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import yummi.wwwbe.domain.user.dto.UserRequestDTO;
import yummi.wwwbe.domain.user.dto.UserResponseDTO;
import yummi.wwwbe.domain.user.service.UserService;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("www/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/join")
    public ResponseEntity<String> joinProcess(@RequestBody UserRequestDTO dto) {

        userService.createOneUser(dto);
        return ResponseEntity.ok("회원 가입 완료");
    }

    @GetMapping("/update/{username}")
    public ResponseEntity<String> updatePage(@PathVariable String username) {

        if (userService.isAccess(username)) {
            UserResponseDTO dto = userService.readOneUser(username);
            return ResponseEntity.ok(dto.toString());
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("요청을 수행할 권한이 없습니다.");
    }

    @PostMapping("/update/{username}")
    public ResponseEntity<String> updateProcess(@PathVariable String username, @RequestBody  UserRequestDTO dto) {

        try {
            if (userService.isAccess(username)) {
                userService.updateOneUser(dto, username);
                return ResponseEntity.ok("유저 정보 업데이트 완료");
            }

            throw new AccessDeniedException("요청을 수행할 권한이 없습니다.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("유저 업데이트 중 오류가 발생했습니다.");
        }
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<String> deleteProcess(@RequestParam String username) {

      try {
          if (userService.isAccess(username)) {
            userService.deleteOneUser(username);
            return ResponseEntity.ok("유저 삭제 완료");
          }
          throw new AccessDeniedException("요청을 수행할 권한이 없습니다.");
      } catch (AccessDeniedException e) {
          return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("유저 삭제 중 오류가 발생했습니다.");
      }
    }
}

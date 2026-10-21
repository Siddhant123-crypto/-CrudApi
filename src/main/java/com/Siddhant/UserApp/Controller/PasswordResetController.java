
package com.Siddhant.UserApp.Controller;

import com.Siddhant.UserApp.dto.ForgotPasswordRequest;
import com.Siddhant.UserApp.dto.ResetPasswordRequest;
import com.Siddhant.UserApp.Service.PasswordResetService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(
            PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    // Forgot Password API
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {
            passwordResetService.forgotPassword(request);

            response.put("message", "If the email is registered, a reset token will be sent.");

            Map<String, Object> data = new HashMap<>();
            data.put("email", request != null ? request.getEmail() : null);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            response.put("data", new HashMap<>());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("message", "Failed to send password reset email: " + e.getMessage());
            response.put("data", new HashMap<>());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Reset Password API
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {
            String message = passwordResetService.resetPassword(request);

            response.put("message", message);

            Map<String, Object> data = new HashMap<>();
            data.put("passwordReset", true);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            response.put("data", new HashMap<>());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("message", "Password reset failed: " + e.getMessage());
            response.put("data", new HashMap<>());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Web landing & app deep-link fallback page
    @GetMapping(value = "/reset-password-page", produces = org.springframework.http.MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String resetPasswordPage(@RequestParam(value = "token", required = false) String token) {
        String safeToken = token != null ? token.trim().replace("\"", "&quot;") : "";
        return "<!DOCTYPE html>"
                + "<html lang=\"en\"><head><meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                + "<title>Farm2Home - Reset Password</title>"
                + "<style>"
                + "  * { box-sizing: border-box; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; }"
                + "  body { background: #F7FAF7; margin: 0; padding: 20px; display: flex; align-items: center; justify-content: center; min-height: 100vh; }"
                + "  .card { background: #ffffff; max-width: 440px; width: 100%; border-radius: 20px; padding: 32px 24px; box-shadow: 0 10px 25px rgba(0,0,0,0.06); border: 1px solid #E8EFE8; text-align: center; }"
                + "  .logo { font-size: 26px; font-weight: 800; color: #1B5E20; margin-bottom: 4px; }"
                + "  .subtitle { font-size: 13.5px; color: #556857; margin-bottom: 24px; }"
                + "  .btn-app { display: block; width: 100%; background: #1B5E20; color: #ffffff; text-decoration: none; padding: 14px 20px; border-radius: 12px; font-weight: 700; font-size: 15px; margin-bottom: 18px; box-shadow: 0 4px 10px rgba(27,94,32,0.2); }"
                + "  .divider { height: 1px; background: #E8EFE8; margin: 24px 0; position: relative; }"
                + "  .divider span { position: absolute; top: -10px; left: 50%; transform: translateX(-50%); background: #ffffff; padding: 0 12px; color: #888; font-size: 12px; }"
                + "  .input-group { text-align: left; margin-bottom: 16px; }"
                + "  label { display: block; font-size: 13px; font-weight: 600; color: #333; margin-bottom: 6px; }"
                + "  input { width: 100%; padding: 12px 14px; border: 1px solid #C8C8C8; border-radius: 10px; font-size: 15px; outline: none; }"
                + "  input:focus { border-color: #2D983A; }"
                + "  .btn-submit { width: 100%; background: #2D983A; color: #fff; border: none; padding: 14px; border-radius: 12px; font-weight: 700; font-size: 15px; cursor: pointer; }"
                + "  .btn-submit:disabled { opacity: 0.6; }"
                + "  .msg-box { padding: 12px; border-radius: 10px; margin-top: 16px; font-size: 13.5px; display: none; }"
                + "  .msg-error { background: #FFEBEE; color: #C62828; border: 1px solid #FFCDD2; }"
                + "  .msg-success { background: #E8F5E9; color: #1B5E20; border: 1px solid #C8E6C9; }"
                + "</style></head><body>"
                + "<div class=\"card\">"
                + "  <div class=\"logo\">🌱 Farm2Home</div>"
                + "  <div class=\"subtitle\">Reset your account password</div>"
                + "  <a id=\"appLink\" href=\"farm2home://reset-password?token=" + safeToken + "\" class=\"btn-app\">Open in Farm2Home App</a>"
                + "  <div class=\"divider\"><span>OR RESET IN BROWSER</span></div>"
                + "  <form id=\"resetForm\" onsubmit=\"submitReset(event)\">"
                + "    <div class=\"input-group\">"
                + "      <label>New Password (min 8 chars)</label>"
                + "      <input type=\"password\" id=\"newPassword\" required minlength=\"8\" placeholder=\"Enter new password\" />"
                + "    </div>"
                + "    <div class=\"input-group\">"
                + "      <label>Confirm Password</label>"
                + "      <input type=\"password\" id=\"confirmPassword\" required minlength=\"8\" placeholder=\"Confirm new password\" />"
                + "    </div>"
                + "    <button type=\"submit\" id=\"submitBtn\" class=\"btn-submit\">Reset Password</button>"
                + "  </form>"
                + "  <div id=\"msgBox\" class=\"msg-box\"></div>"
                + "</div>"
                + "<script>"
                + "  const token = \"" + safeToken + "\";"
                + "  if (token) {"
                + "    setTimeout(function() { window.location.href = \"farm2home://reset-password?token=\" + encodeURIComponent(token); }, 400);"
                + "  }"
                + "  async function submitReset(e) {"
                + "    e.preventDefault();"
                + "    const msg = document.getElementById('msgBox');"
                + "    msg.style.display = 'none';"
                + "    const p1 = document.getElementById('newPassword').value;"
                + "    const p2 = document.getElementById('confirmPassword').value;"
                + "    if (!token) { showMsg('Missing reset token. Please use the link from your email.', true); return; }"
                + "    if (p1 !== p2) { showMsg('Passwords do not match.', true); return; }"
                + "    if (p1.length < 8) { showMsg('Password must contain at least 8 characters.', true); return; }"
                + "    const btn = document.getElementById('submitBtn');"
                + "    btn.disabled = true; btn.innerText = 'Resetting...';"
                + "    try {"
                + "      const res = await fetch('/user/reset-password', {"
                + "        method: 'POST',"
                + "        headers: { 'Content-Type': 'application/json' },"
                + "        body: JSON.stringify({ token: token, newPassword: p1 })"
                + "      });"
                + "      const data = await res.json();"
                + "      if (res.ok) {"
                + "        document.getElementById('resetForm').style.display = 'none';"
                + "        showMsg('✅ Password reset successful! Please open the Farm2Home app and sign in.', false);"
                + "      } else {"
                + "        showMsg(data.message || 'Reset failed.', true);"
                + "      }"
                + "    } catch (err) {"
                + "      showMsg('Network error. Please try again.', true);"
                + "    } finally {"
                + "      btn.disabled = false; btn.innerText = 'Reset Password';"
                + "    }"
                + "  }"
                + "  function showMsg(text, isErr) {"
                + "    const msg = document.getElementById('msgBox');"
                + "    msg.innerText = text;"
                + "    msg.className = 'msg-box ' + (isErr ? 'msg-error' : 'msg-success');"
                + "    msg.style.display = 'block';"
                + "  }"
                + "</script></body></html>";
    }
}

package com.example.demo;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
//import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Repository.room.UserInfoRepository;
import com.example.demo.app.validate.Userform;
import com.example.demo.auth.UserServiceimpl;

@Controller
public class UsersController {

	@ModelAttribute
	Userform setUpForm() {
		return new Userform();
	}

	//	パスワードのハッシュ化に使用
	@Autowired
	PasswordEncoder passwordEncoder;

	//	自作リポジトリとの接続
	@Autowired
	UserInfoRepository userInforepo;

	//	新規作成時のログインで自作のUserserviceを使う
	@Autowired
	UserServiceimpl userService;

	//  新規登録画面からのログイン認証で使用
	//    @Autowired
	//    RequestCache requestCache;

	//  新規登録画面からのログイン認証で使用
	@Autowired
	protected AuthenticationManager authenticationManager;

	//	エラーメッセージの文字化け防止
	@Bean
	public LocalValidatorFactoryBean validator() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		//        書き換え後LocalvalidatorFactoryに保存
		validator.setValidationMessageSource(messageSource());
		return validator;
	}

	//    springで読み込まれているMessageSourceクラスの書き換え
	private MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		//プロパティファイルの名前やディレクトリも変更可能
		messageSource.setBasename("classpath:/ValidationMessages");
		//UTF-8に設定（エラーメッセージの文字化け防止）
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	//	diコンテナを扱うためjdbctemplateの読み込み
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@GetMapping("/users")
	private String get_signin(Model model) {
		model.addAttribute("userform", new Userform());
		//		model.addAttribute("userform", form);

		return "users/test";
	}



    //	新規登録処理
	@PostMapping("/registration")
	private String post_signin(@Validated Userform form,
			BindingResult result,
			RedirectAttributes redirectAttributes,
			Errors errors,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam("username") String username,
			@RequestParam("password") String password,
			Model model) {
		String errorMessages = "";
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
		token.setDetails(new WebAuthenticationDetails(request));//if request is needed during authentication
		Authentication auth = null;
		String sql_select = "select username from  users where username= ?";
		String sql_insert = "insert into users(username, password) values (?, ?)";
		//エラーがあるとき
		if (result.hasErrors()) {
			for (ObjectError error : result.getAllErrors()) {
				// ここでメッセージを取得する。
				errorMessages += error.getDefaultMessage() + "\n";
			}
			System.out.println(result.getAllErrors());
			System.out.println("バリデーションがありました");
			model.addAttribute("result", errorMessages);
			model.addAttribute("username", username);
		} else {
			try {
				String DB_username = jdbcTemplate.queryForObject(sql_select, String.class, username);
				errorMessages = username + "は既に登録済みのメールアドレスですログインしてください";
				redirectAttributes.addFlashAttribute("result", errorMessages);
				redirectAttributes.addFlashAttribute("username", username);
				return "redirect:login";
			} catch (IncorrectResultSizeDataAccessException e) {
				System.out.println("DB登録処理開始");
				try {
					//入力された値が正しいとき(InsertとSecurietyログイン)
					String password_encode = passwordEncoder.encode(password);
					//	Mysqlに値を保存
					jdbcTemplate.update(sql_insert, username, password_encode);
					model.addAttribute("result", username);

					auth = authenticationManager.authenticate(token);

					SecurityContext securityContext = SecurityContextHolder.getContext();
					securityContext.setAuthentication(auth);

					//ServletでSessionを開始する
					HttpSession session = request.getSession(true);

					//  HttpRequestのsessionでSpringSecurityの認証の書き換え
					session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

					return "redirect:signed_in";

				} catch (AuthenticationException error) {
					//Insert失敗時
					System.out.println("Catchからの出力");
					return "users/registration";
				}
			}
		}
		return "users/registration";
	}


     //	エラーメッセージの作成
	@PostMapping("/login")
	public String login_error_get(@Validated Userform form, BindingResult result,
			@ModelAttribute("username") String user,
			@RequestAttribute(name = WebAttributes.AUTHENTICATION_EXCEPTION, required = false) Exception exception,
			Model model, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String errorMessage = "";

        //ログインチェック
		if (session != null) {
			System.out.println("session存在");
		}

		if(result.hasErrors()) {

			for (ObjectError error : result.getAllErrors()) {
				// ここでメッセージを取得する。
				 errorMessage += error.getDefaultMessage() + "\r";
			}
			model.addAttribute("result", errorMessage);
	     }else {
             //	  Postバリデーションを通過したときは該当するアドレスがない認証エラーとみなす
	    	 model.addAttribute("result", "メールアドレスかパスワードが違います");
	     }
		return "users/login";
	}

    //	ログインページ表示
	@GetMapping("/login")
	public String get_login(
			@ModelAttribute("username") String user,
			@RequestAttribute(name = WebAttributes.AUTHENTICATION_EXCEPTION, required = false) Exception exception,
			Model model, HttpServletRequest request,Principal principal) {

		//ログインチェック
		if(principal != null) {
			return "redirect:list";
		}
		model.addAttribute("result", "");
		return "users/login";
	}

    //	エラーメッセージの取得/modelに保存
	@PostMapping("/login_error")
	private String post_login_error(@Validated Userform form, BindingResult result,
			Model model
			) {
		return "";
	}

	//	レコード単位でのMysql検索と表示
	@GetMapping("/registration")
	public String jdbclist(Userform form, String name, ModelMap modelmap,Principal principal) {

		//ログインチェック
		if(principal != null) {
			return "redirect:list";
		}

		String sql = "select id  from users where id = ?";
		//ハッシュに対して配列を入れている
		List<Map<String, Object>> record = jdbcTemplate.queryForList(sql, 3);

		modelmap.addAttribute("Userform", form);
		modelmap.addAttribute("result", name);

		return "users/registration";
	}

	@GetMapping("/signed_in")
	public String get_signed_inl(Userform form, Model model) {
		return "users/signed_in";
	}

    //	登録ユーザー一覧表示
	@GetMapping("/list")
	public String get_list(Userform form, Model model) {
		String SQL = "select username, password from users;";
		String a_username = "";
		String a_hash_password = "";
		List<String> username_list = new ArrayList<String>();
		List<String> password_list = new ArrayList<String>();

		List<Map<String, Object>> list_usernames = jdbcTemplate.queryForList(SQL);
		System.out.println(list_usernames.size());

		//登録しているユーザー数の取得
		int user_count = list_usernames.size();

		for (int i = 0; user_count > i; i += 1) {
			list_usernames.get(i).put("count", i);
		}
		model.addAttribute("list_user", list_usernames);
		model.addAttribute("count", user_count);

		return "users/list";
	}
}
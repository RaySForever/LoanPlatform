package xyz.raysmen.lp.core.hfb;

/**
 * 汇付宝常量定义
 */
public class HfbConst {

    /**
     * 给商户分配的唯一标识
     */
    public static final String AGENT_ID = "999888";
    /**
     * 签名key
     */
    public static final String SIGN_KEY = "9876543210";
    public static final String RESULT_CODE = "resultCode";
    public static final String RESULT_MSG = "resultMsg";


    // 用户绑定方面
    /**
     * 用户绑定汇付宝平台url地址
     */
    public static final String USERBIND_URL = "http://localhost:9999/userBind/BindAgreeUserV2";
    /**
     * 用户绑定异步回调
     */
    public static final String USERBIND_NOTIFY_URL = "http://localhost/api/core/userBind/notify";
    /**
     * 用户绑定同步回调
     */
    public static final String USERBIND_RETURN_URL = "http://localhost:3000/user";

    // 充值方面
    /**
     * 充值汇付宝平台url地址
     */
    public static final String RECHARGE_URL = "http://localhost:9999/userAccount/AgreeBankCharge";
    /**
     * 充值异步回调
     */
    public static final String RECHARGE_NOTIFY_URL = "http://localhost/api/core/userAccount/notify";
    public static final String RECHARGE_SUCCESS_CODE = "0001";
    /**
     * 充值同步回调
     */
    public static final String RECHARGE_RETURN_URL = "http://localhost:3000/user";

    // 投标
    /**
     * 投资汇付宝平台url地址
     */
    public static final String INVEST_URL = "http://localhost:9999/userInvest/AgreeUserVoteProject";
    /**
     * 投资异步回调
     */
    public static final String INVEST_NOTIFY_URL = "http://localhost/api/core/lendItem/notify";
    public static final String INVEST_SUCCESS_CODE = "0001";
    /**
     * 投资同步回调
     */
    public static final String INVEST_RETURN_URL = "http://localhost:3000/user";

    /**
     * 放款
     */
    public static final String MAKE_LOAD_URL = "http://localhost:9999/userInvest/AgreeAccountLendProject";
    public static final String MAKE_LOAD_SUCCESS_CODE = "0000";

    // 提现方面
    /**
     * 提现汇付宝平台url地址
     */
    public static final String WITHDRAW_URL = "http://localhost:9999/userAccount/CashBankManager";
    /**
     * 提现异步回调
     */
    public static final String WITHDRAW_NOTIFY_URL = "http://localhost/api/core/userAccount/notifyWithdraw";
    public static final String WITHDRAW_SUCCESS_CODE = "0001";
    /**
     * 充值同步回调
     */
    public static final String WITHDRAW_RETURN_URL = "http://localhost:3000/user";

    // 还款扣款
    /**
     * 还款扣款汇付宝平台url地址
     */
    public static final String BORROW_RETURN_URL = "http://localhost:9999/lendReturn/AgreeUserRepayment";
    /**
     * 还款扣款异步回调
     */
    public static final String BORROW_RETURN_NOTIFY_URL = "http://localhost/api/core/lendReturn/notifyUrl";
    public static final String BORROW_RETURN_SUCCESS_CODE = "0001";
    /**
     * 还款扣款同步回调
     */
    public static final String BORROW_RETURN_RETURN_URL = "http://localhost:3000/user";

}

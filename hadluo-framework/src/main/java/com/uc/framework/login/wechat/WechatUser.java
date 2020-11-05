package com.uc.framework.login.wechat;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("用户微信信息")
public class WechatUser implements Serializable {
    private static final long serialVersionUID = 361613432596880792L;
    /** 用户的唯一标识 */
    private String openid;
    /** 用户昵称 */
    @ApiModelProperty("用户昵称")
    private String nickname;
    /** 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知 */
    @ApiModelProperty("用户的性别，值为1时是男性，值为2时是女性，值为0时是未知")
    private String sex;
    /** 用户个人资料填写的省份 */
    @ApiModelProperty("用户个人资料填写的省份")
    private String province;
    /** 普通用户个人资料填写的城市 */
    @ApiModelProperty("普通用户个人资料填写的城市")
    private String city;
    /** 国家，如中国为CN */
    @ApiModelProperty("国家，如中国为CN")
    private String country;
    /**
     * 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
     */
    @ApiModelProperty("用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。")
    private String headimgurl;
    /** 用户特权信息，json 数组，如微信沃卡用户为（chinaunicom） */
    private List<String> privilege;
    /** 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。 */
    @ApiModelProperty("unionid 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段")
    private String unionid;

    /** 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同 */
    @ApiModelProperty("网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同")
    private String access_token;

    private Integer errcode;

    private String errmsg;
    
    /**微信id*/
    private String wxId ;

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public List<String> getPrivilege() {
        return privilege;
    }

    public void setPrivilege(List<String> privilege) {
        this.privilege = privilege;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
    
    public String getWxId() {
        return wxId;
    }
    
    public void setWxId(String wxId) {
        this.wxId = wxId;
    }

}

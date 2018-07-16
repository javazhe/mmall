package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by jia on 2018/6/2.
 */
public class Const {

    public static final String CURRENT_USER ="currentUser";
    public static final String USERNAME  ="username";
    public static final String  EMAIL ="email";


    public interface Role{
        int ROLE_CUSTOMER =0;
        int ROLE_ADMIN = 1;

    }
    public interface Cart{
         int CHECKED = 1;
         int UN_CHECKED = 0;
         String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";//限制失败
         String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";//限制成功
    }


     public interface ProductListOrderBy{
         Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_asc","price_desc");
     }

   public enum ProductEnumStatus{
        ON_SALE(1,"在线");
        private String value;
        private Integer code;

        ProductEnumStatus(Integer code,String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public Integer getCode() {
            return code;
        }
    }
}

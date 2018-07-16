package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ResponseService;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by jia on 2018/6/26.
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    public ResponseService<CartVo> add(Integer userId,Integer productId,Integer count){
        if(productId == null || count == null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cartItem = cartMapper.selectByProductIdUserId(userId, productId);
        if (cartItem == null){
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setQuantity(count);
            cart.setChecked(Const.Cart.CHECKED);
            cart.setProductId(productId);
            cartMapper.insert(cart);
        }else{
            cartItem.setQuantity(cartItem.getQuantity()+count);
            cartMapper.updateByPrimaryKeySelective(cartItem);
        }
        CartVo cartVo = this.getCartVoLimit(userId);

        return ResponseService.createBySuccess(cartVo);
    }

     public ResponseService<CartVo> update(Integer userId,Integer productId,Integer count){
         if(productId == null || count == null){
             return ResponseService.createByCodeErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
         }

         Cart cart = cartMapper.selectByProductIdUserId(userId, productId);
         if (cart != null){
             cart.setQuantity( count);
             cartMapper.updateByPrimaryKeySelective(cart);
         }else{
             return ResponseService.createByCodeErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
         }

         CartVo cartVo = this.getCartVoLimit(userId);

         return ResponseService.createBySuccess(cartVo);

     }


     //查询购物车里面所有的商品关于要展示的所有的信息
    public ResponseService<CartVo> list(Integer userId){
        CartVo cartVo = this.getCartVoLimit(userId);
        return ResponseService.createBySuccess(cartVo);

    }

    //删除购物车中的Product的方法个方法 productIds 1,3,4,5,6
    public ResponseService<CartVo> deleteProduct(Integer userId ,String productIds){
       //// TODO: 2018/7/4 包含的id都删了 根据传入的字符串,进行分割 取出每个id
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        cartMapper.deleteByUserIdProductIds(userId,productIdList);
        return this.list(userId);
    }

    public ResponseService<CartVo> selectOrUnSelect(Integer userId,Integer productId,Integer checked){
        cartMapper.checkedOrUncheckedProduct(userId,productId,checked);
        return this.list(userId);
    }

    public ResponseService<Integer> getCartProductCount(Integer userId){
        if(userId == null){
            return ResponseService.createBySuccess(0);
        }
        return ResponseService.createBySuccess(cartMapper.selectCartProductCount(userId));
    }




    //非常关键的购物车方法,填充cartproductVo ,进而填充cartVo,返回的就是填充的cartVo 即设置根据购物车数量限制,计算勾选的购物车的总价
    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");
        if (CollectionUtils.isNotEmpty(cartList)){
            for (Cart item:cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(item.getProductId());
                cartProductVo.setId(item.getId());
                cartProductVo.setQuantity(item.getQuantity());
                cartProductVo.setProductChecked(item.getChecked());
                Product product = productMapper.selectByPrimaryKey(item.getProductId());
                if (product != null){
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount = 0;
                    if (product.getStock() >= item.getQuantity()){
                        //库存允许购买
                        buyLimitCount = item.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);//设置商品数量限制成功
                    }else{
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(item.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);

                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算购物车中该商品的总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(item.getChecked());

                    if (cartProductVo.getProductChecked() == Const.Cart.CHECKED){
                        cartTotalPrice = BigDecimalUtil.add( cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                    }
                    cartProductVoList.add(cartProductVo);
                }
            }
            cartVo.setCartProductVoList(cartProductVoList);
            cartVo.setCartTotalPrice(cartTotalPrice);
            cartVo.setAllChecked(this.getAllCheckedStatus(userId));
            cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
            return cartVo;
        }
        return null;//异常
    }

    private boolean getAllCheckedStatus(Integer userId){
        if(userId == null){
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }


}

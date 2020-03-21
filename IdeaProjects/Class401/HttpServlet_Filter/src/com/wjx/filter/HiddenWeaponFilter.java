package com.wjx.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * @Auther:wjx
 * @Date:2019/5/13
 * @Description:com.wjx.filter
 * @version:1.0
 */
public class HiddenWeaponFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("HiddenWeaponFilter.init 我们是检查暗器的，准备上班了！");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("HiddenWeaponFilter.doFilter 我们检查暗器的正在上班，请交出暗器！");
        chain.doFilter(request, response);
        System.out.println("HiddenWeaponFilter.doFilter 我们检查暗器的正在上班，还有没有没上交的暗器了？！");
    }

    @Override
    public void destroy() {
        System.out.println("HiddenWeaponFilter.destroy 我们是检查暗器的，下班了！");
    }
}

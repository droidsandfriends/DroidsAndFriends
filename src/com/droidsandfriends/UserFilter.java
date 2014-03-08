package com.droidsandfriends;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class UserFilter implements Filter {
  
  @Override
  public void init(FilterConfig config) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      User user = userService.getCurrentUser();
      boolean isAdmin = userService.isUserAdmin();
      request.setAttribute("userId", user.getUserId());
      request.setAttribute("username", user.getEmail());
      request.setAttribute("isAdmin", isAdmin);
      request.setAttribute("logoutUrl", userService.createLogoutURL(request.getRequestURI()));

      // TODO: Optimize these, for god's sake!
      request.setAttribute("propertyMap", Property.getPropertyMap());
      Map<String, List<Option>> optionMap = new HashMap<String, List<Option>>();
      optionMap.put(Property.MEMBERSHIP_STATUS.toString(), MembershipStatus.getOptionList());
      optionMap.put(Property.EXPERIENCE.toString(), Experience.getOptionList());
      optionMap.put(Property.GAS_CARD.toString(), GasCard.getOptionList());
      optionMap.put(Property.VENUE.toString(), Venue.getOptionList());
      request.setAttribute("optionMap", optionMap);
   } else {
      String requestUri = request.getRequestURI();
      String loginUrl = userService.createLoginURL(requestUri);
      if (!loginUrl.startsWith(requestUri)) {
        // Redirect to login page.
        response.sendRedirect(loginUrl);
        return;
      }
    }

    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
  }

}

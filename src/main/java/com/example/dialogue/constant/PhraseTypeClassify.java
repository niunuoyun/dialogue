package com.example.dialogue.constant;

/**
 * @author Huihua Niu
 * on 2020/1/8 14:01
 */
public class PhraseTypeClassify {
  //主語实体类型
  public static String[] entityType = {"nr1","nr","nr2","vn","nrf","ns","nz","nt","nsf","idiomatic","vehicle","subway","idiom","proverb","proverb","plant","chemical","company","food","festival","animal","carbrand","carparts","position"};
  //结束语实体类型
  public static String[] endEntityType = {"nr1","nr","nr2","n","nrf","ns","nz","nt","nsf","idiomatic","vehicle","subway","idiom","proverb","proverb","plant","chemical","company","food","festival","animal","carbrand","carparts","position"};

  //额外的实体补充
  public static String[] keyType = {"a","v","vn","t"};
  public static String[] predicateType = {"predicate","a","v","t"};
  public static String[] additionalType = {"j","d","adv","f","c","p","b"};
}

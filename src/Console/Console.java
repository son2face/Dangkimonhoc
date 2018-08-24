package Console;

import Crawler.CrawlerService;
import Subject.ObjectModel;
import Subject.ParseObjectService;
import com.google.common.collect.Lists;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Console {

    public static void main(String[] args) {
        List<Integer> n = Lists.newArrayList(1,2,3);
        n.parallelStream().forEach(integer -> {
            if (integer == 1) {
                assign("15021394", "1p02en", Lists.newArrayList("INT3115"));
                openBrowser("15021394", "1p02en");
            }
//            else if (integer == 3) {
//                assign("15021716", "12341997", Lists.newArrayList( "INT3209 1"));
//                openBrowser("15021716", "12341997");
////            }
//            }
// else if (integer == 3) {
//                assign("17020070", "17020070", Lists.newArrayList("PES","PES","PES"));
//                openBrowser("17020070", "17020070");
//            }
// else if (integer == 2) {
//                assign("17020185", "17020185", Lists.newArrayList("INT3301 2", "INT 2020 2"));
//                openBrowser("17020185", "17020185");17020667 17021131
//            }
        });
    }

    private static void assign(String studentCode, String passWord, List<String> subject) {
        System.setProperty("webdriver.chrome.driver", "/home/son/Desktop/Dangkimonhoc/chromedriver");
        CrawlerService crawlerService = new CrawlerService(studentCode, passWord);
        while ((crawlerService.login() || crawlerService.checkBusy()) && subject.size() > 0) {
            List<ObjectModel> objectModelList;
            try {
                objectModelList = ParseObjectService.Parse(crawlerService.getSubject());
                System.out.println("checking....." + objectModelList.size());
                System.out.println("Assign for: " + subject);
                for (ObjectModel objectModel : objectModelList) {
                    subject = subject.parallelStream().filter(s -> {
                        if (objectModel.code.equals(s)) {
                            try {
                                crawlerService.getData();
                                crawlerService.register(objectModel);
                                boolean isCommited = crawlerService.commit();
                                crawlerService.logout();
                                return !isCommited;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                    }).collect(Collectors.toList());
                }
//                Thread.sleep(500);?
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        return true;
    }

    private static void openBrowser(String mssv, String passWord) {
        CrawlerService crawlerService = new CrawlerService(mssv, passWord);
        try {
            crawlerService.login();
            WebDriver webDriver2 = new ChromeDriver();
            Date date = new Date();
            date.setTime(date.getTime() + Long.valueOf("86400000"));
            webDriver2.get("http://dangkyhoc.vnu.edu.vn/dang-ky-mon-hoc-nganh-1");
            webDriver2.manage().addCookie(new Cookie("ASP.NET_SessionId", crawlerService.asp.substring(18), "/", date));
            webDriver2.manage().addCookie(new Cookie("__RequestVerificationToken", crawlerService.request.substring("__RequestVerificationToken".length() + 1), "vnu.edu.vn", "/", date));
            webDriver2.get("http://dangkyhoc.vnu.edu.vn/dang-ky-mon-hoc-nganh-1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

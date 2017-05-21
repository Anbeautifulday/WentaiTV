package cn.edu.cqupt.wentaitv.json;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wentai on 17-5-19.
 */

public class VideoInfo {

    private int showapi_res_code;
    private String showapi_res_error;
    private ShowBody showapi_res_body;

    public int getShowapi_res_code() {
        return showapi_res_code;
    }

    public void setShowapi_res_code(int showapi_res_code) {
        this.showapi_res_code = showapi_res_code;
    }

    public String getShowapi_res_error() {
        return showapi_res_error;
    }

    public void setShowapi_res_error(String showapi_res_error) {
        this.showapi_res_error = showapi_res_error;
    }

    public ShowBody getShowapi_res_body() {
        return showapi_res_body;
    }

    public void setShowapi_res_body(ShowBody showapi_res_body) {
        this.showapi_res_body = showapi_res_body;
    }
    //********************************************************************
    public class ShowBody {
        private int ret_code;
        private PageBean pagebean;

        public int getRet_code() {
            return ret_code;
        }

        public void setRet_code(int ret_code) {
            this.ret_code = ret_code;
        }

        public PageBean getPageBean() {
            return pagebean;
        }

        public void setPageBean(PageBean pageBean) {
            this.pagebean = pageBean;
        }
        //*********************************************************
        public class PageBean {
            private int allPages;
            private int currentPage;
            private int allNum;
            private int maxResult;
            private List<BodyInfo> contentlist;

            public int getAllPages() {
                return allPages;
            }

            public void setAllPages(int allPages) {
                this.allPages = allPages;
            }

            public List<BodyInfo> getContentlist() {
                return contentlist;
            }

            public int getCurrentPage() {
                return currentPage;
            }

            public void setCurrentPage(int currentPage) {
                this.currentPage = currentPage;
            }

            public int getAllNum() {
                return allNum;
            }

            public void setAllNum(int allNum) {
                this.allNum = allNum;
            }

            public int getMaxResult() {
                return maxResult;
            }

            public void setMaxResult(int maxResult) {
                this.maxResult = maxResult;
            }

            public void setContentlist(List<BodyInfo> contentlist) {
                this.contentlist = contentlist;
            }

            //**********************************************************************
            public class BodyInfo implements Serializable {
                private String id;
                private String love;

                private String name;
                private String text;
                private String profile_image;
                private String video_uri;
                private String create_time;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getLove() {
                    return love;
                }

                public void setLove(String love) {
                    this.love = love;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public String getProfile_image() {
                    return profile_image;
                }

                public void setProfile_image(String profile_image) {
                    this.profile_image = profile_image;
                }

                public String getVideo_uri() {
                    return video_uri;
                }

                public void setVideo_uri(String video_uri) {
                    this.video_uri = video_uri;
                }

                public String getCreate_time() {
                    return create_time;
                }

                public void setCreate_time(String create_time) {
                    this.create_time = create_time;
                }
            }
        }
    }

}

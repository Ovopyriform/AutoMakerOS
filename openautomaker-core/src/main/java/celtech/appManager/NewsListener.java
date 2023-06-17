package celtech.appManager;

import java.util.List;

import celtech.appManager.NewsBot.NewsArticle;

/**
 *
 * @author Ian
 */
public interface NewsListener
{
	public void hereIsTheNews(List<NewsArticle> newsArticles);
}

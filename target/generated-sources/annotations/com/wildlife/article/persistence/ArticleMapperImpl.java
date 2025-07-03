package com.wildlife.article.persistence;

import com.wildlife.article.api.ArticleDto;
import com.wildlife.article.core.Article;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-03T04:57:51+0530",
    comments = "version: 1.6.2, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class ArticleMapperImpl extends ArticleMapper {

    @Override
    public ArticleDto toDto(Article article) {
        if ( article == null ) {
            return null;
        }

        ArticleDto articleDto = new ArticleDto();

        articleDto.setAuthorId( article.getAuthorId() );
        articleDto.setImages( stringToImageList( article.getImages() ) );
        articleDto.setVideos( stringToVideoList( article.getVideos() ) );
        articleDto.setId( article.getId() );
        articleDto.setTitle( article.getTitle() );
        articleDto.setContent( article.getContent() );
        articleDto.setExcerpt( article.getExcerpt() );
        articleDto.setPublished( article.getPublished() );
        articleDto.setFeatured( article.getFeatured() );
        articleDto.setCategory( article.getCategory() );
        articleDto.setViews( article.getViews() );
        List<String> list2 = article.getTags();
        if ( list2 != null ) {
            articleDto.setTags( new ArrayList<String>( list2 ) );
        }
        articleDto.setPublishDate( article.getPublishDate() );
        articleDto.setCreatedAt( article.getCreatedAt() );
        articleDto.setUpdatedAt( article.getUpdatedAt() );

        return articleDto;
    }

    @Override
    public Article toEntity(ArticleDto articleDto) {
        if ( articleDto == null ) {
            return null;
        }

        Article article = new Article();

        article.setImages( imageListToString( articleDto.getImages() ) );
        article.setVideos( videoListToString( articleDto.getVideos() ) );
        article.setId( articleDto.getId() );
        article.setTitle( articleDto.getTitle() );
        article.setContent( articleDto.getContent() );
        article.setExcerpt( articleDto.getExcerpt() );
        article.setPublished( articleDto.getPublished() );
        article.setFeatured( articleDto.getFeatured() );
        article.setCategory( articleDto.getCategory() );
        article.setViews( articleDto.getViews() );
        List<String> list = articleDto.getTags();
        if ( list != null ) {
            article.setTags( new ArrayList<String>( list ) );
        }

        return article;
    }

    @Override
    public List<ArticleDto> toDto(List<Article> articles) {
        if ( articles == null ) {
            return null;
        }

        List<ArticleDto> list = new ArrayList<ArticleDto>( articles.size() );
        for ( Article article : articles ) {
            list.add( toDto( article ) );
        }

        return list;
    }

    @Override
    public List<Article> toEntity(List<ArticleDto> articleDtos) {
        if ( articleDtos == null ) {
            return null;
        }

        List<Article> list = new ArrayList<Article>( articleDtos.size() );
        for ( ArticleDto articleDto : articleDtos ) {
            list.add( toEntity( articleDto ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDto(ArticleDto articleDto, Article article) {
        if ( articleDto == null ) {
            return;
        }

        if ( articleDto.getImages() != null ) {
            article.setImages( imageListToString( articleDto.getImages() ) );
        }
        if ( articleDto.getVideos() != null ) {
            article.setVideos( videoListToString( articleDto.getVideos() ) );
        }
        if ( articleDto.getTitle() != null ) {
            article.setTitle( articleDto.getTitle() );
        }
        if ( articleDto.getContent() != null ) {
            article.setContent( articleDto.getContent() );
        }
        if ( articleDto.getExcerpt() != null ) {
            article.setExcerpt( articleDto.getExcerpt() );
        }
        if ( articleDto.getPublished() != null ) {
            article.setPublished( articleDto.getPublished() );
        }
        if ( articleDto.getFeatured() != null ) {
            article.setFeatured( articleDto.getFeatured() );
        }
        if ( articleDto.getCategory() != null ) {
            article.setCategory( articleDto.getCategory() );
        }
        if ( article.getTags() != null ) {
            List<String> list = articleDto.getTags();
            if ( list != null ) {
                article.getTags().clear();
                article.getTags().addAll( list );
            }
        }
        else {
            List<String> list = articleDto.getTags();
            if ( list != null ) {
                article.setTags( new ArrayList<String>( list ) );
            }
        }
    }
}

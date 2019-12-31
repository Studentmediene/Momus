/*
 * Copyright 2016 Studentmediene i Trondheim AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.dusken.momus.article.type;

import lombok.extern.slf4j.Slf4j;
import no.dusken.momus.common.exceptions.RestException;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

@Service
@Slf4j
public class ArticleTypeService {
    private final ArticleTypeRepository articleTypeRepository;

    public ArticleTypeService(ArticleTypeRepository articleTypeRepository) {
        this.articleTypeRepository = articleTypeRepository;
    }

    public ArticleType getArticleTypeById(Long id) {
        return articleTypeRepository.findById(id)
            .orElseThrow(() -> new RestException("Article type with id=" + id + " not found", HttpServletResponse.SC_NOT_FOUND));
    }

    public List<ArticleType> getAllArticleTypes() {
        return articleTypeRepository.findAll();
    }

    public ArticleType createArticleType(ArticleType articleType){
        ArticleType newArticleType = articleTypeRepository.saveAndFlush(articleType);
        log.info("ArticleType with id {} created with data: {}", newArticleType.getId(), newArticleType);
        return newArticleType;
    }

    public ArticleType updateArticleType(Long id, ArticleType articleType) {
        ArticleType existing = getArticleTypeById(id);

        existing.setName(articleType.getName());
        existing.setDescription(articleType.getDescription());
        existing.setDeleted(articleType.isDeleted());

        log.info("ArticleType with id {} updated, data: {}", articleType.getId(), articleType);
        return articleTypeRepository.saveAndFlush(existing);
    }
}

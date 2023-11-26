package data;

import jakarta.persistence.*;
import model.Author;
import model.Category;

import java.util.List;

public class AuthorDB {
    public static String generateId() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try
        {
            String lastId;
            try
            {
                TypedQuery<String> query = em.createQuery(
                        "SELECT a.authorID FROM Author a ORDER BY a.authorID DESC", String.class);
                query.setMaxResults(1);
                lastId = query.getSingleResult();
            }catch(NoResultException e)
            {
                return "AUTH0000";
            }
            int number = Integer.parseInt(lastId.substring(4));
            number++; // Tăng giá trị số lên 1
            String newId = String.format("AUTH%04d", number);
            return newId;
        }catch(Exception e)
        {
            throw new RuntimeException("CREATE NEW ID FAIL", e);
        }
        finally {
            em.close();
        }
    }

    public static void insertAuthor(String authorName, StringBuilder error) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        String queryString = "SELECT a FROM Author a WHERE LOWER(a.authorName) = LOWER(:name) OR UPPER(a.authorName) = UPPER(:name)";
        Query query = em.createQuery(queryString, Author.class);
        query.setParameter("name", authorName );
        List<Author> authors = query.getResultList();
        if(authors.size() == 0)
        {
            trans.begin();
            try {
                Author author = new Author();
                author.setAuthorID(generateId());
                author.setAuthorName(authorName);
                em.persist(author);
                error.append("INSERT SUCCESSFULLY ");
                trans.commit();
            } catch (Exception e) {
                System.out.println(e);
                trans.rollback();
            } finally {
                em.close();
            }
        }else
        {
            error.append("THIS AUTHOR NAME EXISTED ");
            em.close();
        }
    }

    public static List<Author> getAuthorList(){
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try
        {
            String queryString = "SELECT a FROM Author a ORDER BY a.authorID ASC";
            Query query = em.createQuery(queryString, Author.class);
            List<Author> rows = query.getResultList();
            return rows;
        }
        catch (Exception e)
        {
            System.out.println(e);
            throw new RuntimeException("CANNOT GET AUTHORS", e);
        }
        finally {
            em.close();
        }
    }

    public static List<Author> searchAuthor(String authorName, StringBuilder error)
    {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try
        {
            String queryString = "SELECT a FROM Author a WHERE LOWER(a.authorName) LIKE LOWER(:name) OR UPPER(a.authorName) LIKE UPPER(:name) ORDER BY a.authorID ASC";
            Query query = em.createQuery(queryString, Author.class);
            query.setParameter("name", "%" + authorName + "%");
            List<Author> authors = query.getResultList();
            if(authors.size() == 0 )
            {
                error.append("AUTHOR DOES NOT EXIST ");
                return null;
            }
            else return authors;
        }
        catch (Exception e)
        {
            throw new RuntimeException("CANNOT GET FIND", e);
        }
        finally {
            em.close();
        }
    }

    public static void deleteAuthor(String authorID, StringBuilder error) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        try {
            String queryString = "SELECT a FROM Author a where a.authorID = :authorID";
            Query query = em.createQuery(queryString, Author.class);
            query.setParameter("authorID", authorID);
            try {
                Author author = (Author) query.getSingleResult();
                em.remove(author);
                trans.commit();
            } catch (NoResultException e) {
                error.append("AUTHOR ID DOES NOT EXIST ");
            }
        } catch (Exception e) {
            trans.rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }
    public static void updateAuthor(String authorID, String authorName, StringBuilder error) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        String queryString = "SELECT a FROM Author a WHERE LOWER(a.authorName) = LOWER(:name) OR UPPER(a.authorName) = UPPER(:name)";
        Query query = em.createQuery(queryString, Author.class);
        query.setParameter("name", authorName );
        List<Author> authors = query.getResultList();
        if(authors.size() == 0)
        {
            trans.begin();
            try {
                String queryString1 = "SELECT a FROM Author a where a.authorID = :authorID";
                Query query1 = em.createQuery(queryString1, Author.class);
                query1.setParameter("authorID", authorID);
                try {
                    Author author = (Author) query1.getSingleResult();
                    author.setAuthorName(authorName);
                    em.merge(author);
                    trans.commit();
                } catch (NoResultException e) {
                    error.append("AUTHOR ID DOES NOT EXIST ");
                }
            } catch (Exception e) {
                trans.rollback();
                throw new RuntimeException(e);
            } finally {
                em.close();
            }
        }else
        {
            error.append("AUTHOR EXISTED ");
            em.close();
        }
    }
}

import { getFilterQuery } from 'components/filters/utils';
import { getDefaultOptions, request, getUrl } from 'api/helpers';

const resource = 'api/tks';

export const apiTksDelete = async (serviceUrl, id) => {
  const url = `${serviceUrl}/${resource}/${id}`;
  const options = {
    ...getDefaultOptions(),
    method: 'DELETE',
  };
  return request(url, options);
};

export const apiTksGet = async (serviceUrl, { filters = [], pagination, mode }) => {
  const filterQuery = getFilterQuery(filters);
  const paginationQuery = pagination
    ? `page=${pagination.page}&size=${pagination.rowsPerPage}`
    : '';
  const url = getUrl(
    `${serviceUrl}/${resource}${mode === 'count' ? '/count' : ''}`,
    filterQuery,
    paginationQuery
  );
  const options = {
    ...getDefaultOptions(),
    method: 'GET',
  };

  return request(url, options);
};

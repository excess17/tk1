import { getDefaultOptions, request } from 'api/helpers';

const resource = 'api/tks';

export const apiTkGet = async (serviceUrl, id) => {
  const url = `${serviceUrl}/${resource}/${id}`;
  const options = {
    ...getDefaultOptions(),
    method: 'GET',
  };
  return request(url, options);
};

export const apiTkPost = async (serviceUrl, tk) => {
  const url = `${serviceUrl}/${resource}`;
  const options = {
    ...getDefaultOptions(),
    method: 'POST',
    body: tk ? JSON.stringify(tk) : null,
  };
  return request(url, options);
};

export const apiTkPut = async (serviceUrl, id, tk) => {
  const url = `${serviceUrl}/${resource}/${id}`;
  const options = {
    ...getDefaultOptions(),
    method: 'PUT',
    body: tk ? JSON.stringify(tk) : null,
  };
  return request(url, options);
};

export const apiTkDelete = async (serviceUrl, id) => {
  const url = `${serviceUrl}/${resource}/${id}`;
  const options = {
    ...getDefaultOptions(),
    method: 'DELETE',
  };
  return request(url, options);
};

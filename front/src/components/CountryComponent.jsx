import React, {useId, useState, useEffect} from 'react';
import {useNavigate, useParams} from "react-router-dom";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faChevronLeft, faSave} from "@fortawesome/free-solid-svg-icons";
import {alertActions} from "../utils/Rdx";
import {connect, useDispatch} from "react-redux";
import BackendService from "../services/BackendService";

const CountryComponent = props => {

    const [Name, setName] = useState([]);
    const [hidden, setHidden] = useState(false);
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const nameId = useId();
    const params = useParams();

    const loadCountry = () => {
        BackendService.retrieveCountry(params.id)
            .then(
                resp => {
                    setName(resp.data.name);
                    setHidden(false )
                })
            .catch(()=> { setHidden(true )})
    }

    useEffect(() => {
        if (parseInt(params.id) != -1) {
            loadCountry();
        }
    }, [])

    const onSubmit = e => {
        e.preventDefault();
        const form = e.target;
        const formData = new FormData(form);
        const formJson = Object.fromEntries(formData.entries());
        let err = null;
        if (!formJson.name) {
            err = "Название страны должно быть указано";
        }
        if (err) {
            dispatch(alertActions.error(err))
        }
        let country = { id: params.id, name: formJson.name};
        if (parseInt(country.id) === -1) {
            BackendService.createCountry(country)
                .then(()=> navigate("/countries"))
                .catch(()=> {})
        }
        else {
            BackendService.updateCountry(country)
                .then(()=> navigate("/countries"))
                .catch(()=>{})
        }
    }

    if (hidden)
        return null;
    return (
        <div className="container">
            <div className="row">
                <div className="col-md-6">
                    <h3>Страна</h3>
                </div>
                <div className="col-md-6 clearfix">
                    <button className="btn btn-outline-secondary float-end"
                            onClick={ () => { navigate(-1) } }>
                        <FontAwesomeIcon icon={faChevronLeft}/>{' '}Назад</button>
                </div>
            </div>
            <div className="row">
                <div className="col-md-12">
                    <form method="post" onSubmit={onSubmit}>
                        <label className="form-label" htmlFor={nameId}>Название:</label>
                        <input id={nameId} name="name"
                               className="form-control"
                               defaultValue={Name}
                               autoComplete="off"/>
                        <button
                            className="btn btn-outline-secondary mt-4"
                            type="submit">
                            <FontAwesomeIcon icon={faSave}/>{' '}Сохранить</button>
                    </form>
                </div>
            </div>
        </div>
    )
}

export default connect()(CountryComponent);